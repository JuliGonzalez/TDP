import pandas as pd
import numpy as np
import sys
import sklearn
from sklearn.preprocessing import LabelEncoder, OneHotEncoder


class FeatureSelection(object):

    def __init__(self):
        self.col_names = ["duration", "protocol_type", "service", "flag", "src_bytes",
                          "dst_bytes", "land", "wrong_fragment", "urgent", "hot", "num_failed_logins",
                          "logged_in", "num_compromised", "root_shell", "su_attempted", "num_root",
                          "num_file_creations", "num_shells", "num_access_files", "num_outbound_cmds",
                          "is_host_login", "is_guest_login", "count", "srv_count", "serror_rate",
                          "srv_serror_rate", "rerror_rate", "srv_rerror_rate", "same_srv_rate",
                          "diff_srv_rate", "srv_diff_host_rate", "dst_host_count", "dst_host_srv_count",
                          "dst_host_same_srv_rate", "dst_host_diff_srv_rate", "dst_host_same_src_port_rate",
                          "dst_host_srv_diff_host_rate", "dst_host_serror_rate", "dst_host_srv_serror_rate",
                          "dst_host_rerror_rate", "dst_host_srv_rerror_rate", "label"]
        self.df = pd.read_csv("../../../input/KDDTrain+_2.csv", header=None, names=self.col_names)
        self.df_test = pd.read_csv("../../../input/KDDTest+_2.csv", header=None, names=self.col_names)
        self.replacement_dict = {'normal': 0, 'neptune': 1, 'back': 1, 'land': 1, 'pod': 1, 'smurf': 1,
                                 'teardrop': 1, 'mailbomb': 1, 'apache2': 1, 'processtable': 1, 'udpstorm': 1,
                                 'worm': 1, 'ipsweep': 2, 'nmap': 2, 'portsweep': 2, 'satan': 2, 'mscan': 2,
                                 'saint': 2, 'ftp_write': 3, 'guess_passwd': 3, 'imap': 3, 'multihop': 3, 'phf': 3,
                                 'spy': 3, 'warezclient': 3, 'warezmaster': 3, 'sendmail': 3, 'named': 3,
                                 'snmpgetattack': 3, 'snmpguess': 3, 'xlock': 3, 'xsnoop': 3, 'httptunnel': 3,
                                 'buffer_overflow': 4, 'loadmodule': 4, 'perl': 4, 'rootkit': 4, 'ps': 4,
                                 'sqlattack': 4, 'xterm': 4}
        self.col_names_DoS = []
        self.col_names_test_DoS = []

    def show_info_from_dfs(self):
        print("df shape:", self.df.shape)
        print("df test shape", self.df_test.shape)
        print("df head:", self.df.head())
        print(self.df.describe())
        print('Label distribution Training set:')
        print(self.df['label'].value_counts())
        print()
        print('Label distribution Test set:')
        print(self.df_test['label'].value_counts())
        print('Training set:')
        for col_name in self.df.columns:
            if self.df[col_name].dtypes == 'object':
                unique_cat = len(self.df[col_name].unique())
                print(
                    "Feature '{col_name}' has {unique_cat} categories".format(col_name=col_name, unique_cat=unique_cat))
        print()
        print('Distribution of categories in service:')
        print(self.df['service'].value_counts().sort_values(ascending=False).head())

    @staticmethod
    def extract_dummy_column_names(df, df_test):
        string1 = 'Protocol_type_'
        string2 = 'service_'
        string3 = 'flag_'
        unique_protocol = sorted(df.protocol_type.unique())
        unique_protocol_dummies = [string1 + x for x in unique_protocol]
        unique_service = sorted(df.service.unique())
        unique_service_dummies = [string2 + x for x in unique_service]
        unique_flag = sorted(df.flag.unique())
        unique_flag_dummies = [string3 + x for x in unique_flag]
        dummy_cols = unique_protocol_dummies + unique_service_dummies + unique_flag_dummies
        # for testing dataset
        unique_service_test = sorted(df_test.service.unique())
        unique_service_test_dummies = [string2 + x for x in unique_service_test]
        test_dummy_cols = unique_protocol_dummies + unique_service_test_dummies + unique_flag_dummies
        return dummy_cols, test_dummy_cols

    def transform_df_to_non_categorical(self):
        categorical_columns = ['protocol_type', 'service', 'flag']
        df_categorical_columns = self.df[categorical_columns]
        testdf_categorical_columns = self.df_test[categorical_columns]
        df_categorical_columns_enc = df_categorical_columns.apply(LabelEncoder().fit_transform)
        testdf_categorical_columns_enc = testdf_categorical_columns.apply(LabelEncoder().fit_transform)
        # apply OneHotEncoder
        enc = OneHotEncoder()
        df_categorical_columns_encenc = enc.fit_transform(df_categorical_columns_enc)
        dummy_cols, test_dummy_cols = self.extract_dummy_column_names(self.df, self.df_test)
        df_cat_data = pd.DataFrame(df_categorical_columns_encenc.toarray(), columns=dummy_cols)
        testdf_categorical_columns_encenc = enc.fit_transform(testdf_categorical_columns_enc)
        testdf_cat_data = pd.DataFrame(testdf_categorical_columns_encenc.toarray(), columns=test_dummy_cols)
        # add 6 missing from test DF
        trainservice = self.df['service'].tolist()
        testservice = self.df_test['service'].tolist()
        difference = list(set(trainservice) - set(testservice))
        string = 'service_'
        difference = [string + x for x in difference]
        for col in difference:
            testdf_cat_data[col] = 0

        return df_cat_data, testdf_cat_data

    def join_non_categorical_with_categorical_dataframes(self, df_cat_data, test_df_cat_data):
        newdf = self.df.join(df_cat_data)
        newdf.drop('flag', axis=1, inplace=True)
        newdf.drop('protocol_type', axis=1, inplace=True)
        newdf.drop('service', axis=1, inplace=True)

        newdf_test = self.df_test.join(test_df_cat_data)
        newdf_test.drop('flag', axis=1, inplace=True)
        newdf_test.drop('protocol_type', axis=1, inplace=True)
        newdf_test.drop('service', axis=1, inplace=True)
        # print(newdf.shape)
        # print(newdf_test.shape)
        return newdf, newdf_test

    def split_dataframes_in_attack_categories(self, df, test_df):
        labeldf = df['label']
        labeldf_test = test_df['label']
        newlabeldf = labeldf.replace(self.replacement_dict)
        newlabeldf_test = labeldf_test.replace(self.replacement_dict)
        # put the new label column back
        df['label'] = newlabeldf
        test_df['label'] = newlabeldf_test
        print(df['label'].head())
        to_drop_DoS = [2, 3, 4]
        to_drop_Probe = [1, 3, 4]
        to_drop_R2L = [1, 2, 4]
        to_drop_U2R = [1, 2, 3]
        DoS_df = df[~df['label'].isin(to_drop_DoS)]
        Probe_df = df[~df['label'].isin(to_drop_Probe)]
        R2L_df = df[~df['label'].isin(to_drop_R2L)]
        U2R_df = df[~df['label'].isin(to_drop_U2R)]

        # testDF
        DoS_df_test = test_df[~test_df['label'].isin(to_drop_DoS)]
        Probe_df_test = test_df[~test_df['label'].isin(to_drop_Probe)]
        R2L_df_test = test_df[~test_df['label'].isin(to_drop_R2L)]
        U2R_df_test = test_df[~test_df['label'].isin(to_drop_U2R)]
        # print('Train:')
        # print('Dimensions of DoS:', DoS_df.shape)
        # print('Dimensions of Probe:', Probe_df.shape)
        # print('Dimensions of R2L:', R2L_df.shape)
        # print('Dimensions of U2R:', U2R_df.shape)
        # print('Test:')
        # print('Dimensions of DoS:', DoS_df_test.shape)
        # print('Dimensions of Probe:', Probe_df_test.shape)
        # print('Dimensions of R2L:', R2L_df_test.shape)
        # print('Dimensions of U2R:', U2R_df_test.shape)


if __name__ == "__main__":
    feature = FeatureSelection()
    feature.show_info_from_dfs()
    df_cat_data, test_df_cat_data = feature.transform_df_to_non_categorical()
    newdf, newdf_test = feature.join_non_categorical_with_categorical_dataframes(df_cat_data, test_df_cat_data)
    feature.split_dataframes_in_attack_categories(newdf, newdf_test)