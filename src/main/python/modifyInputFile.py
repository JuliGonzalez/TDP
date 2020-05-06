#!/usr/bin/
# -*- coding: utf-8 -*-
"""
Script designed to change the structure of the input file to match the
Naives-Bayes algorithm
"""
import pandas as pd
from sklearn.naive_bayes import GaussianNB
from sklearn.model_selection import train_test_split
from sklearn.metrics import confusion_matrix


class ModifyInputFile(object):
    def __init__(self, file_path):
        self.file_path = file_path
        self.header = ['Duration', 'Protocol_type', 'Service', 'Flag', 'Src_bytes', 'Dst_bytes',
                        'Land', 'Wrong_fragment', 'Urgent', 'Hot', 'Num_failed_logins', 'Logged_in',
                        'Num_compromised', 'Root_shell', 'Su_attempt', 'Num_root', 'Num_file_creations',
                        'Num_shells', 'Num_access_files', 'Num_outbound_cmds', 'Is_hot_login',
                        'Is_guest_login', 'Count', 'Srv_count', 'Serror_rate', 'Srv_serror_rate', 
                        'Rerror_rate', 'Srv_rerror_rate', 'Diff_srv_rate', 'Srv_diff_host_rate',
                        'Dst_host_count', 'Dst_host_srv_count', 'Dst_host_same_srv_rate',
                        'Dst_host_diff_srv_rate', 'Dst_host_same_src_port_rate', 'Dst_host_srv_diff_host_rate',
                        'Dst_host_serror_rate', 'Dst_host_srv_serror_rate', 'Dst_host_rerror_rate',
                        'Dst_host_srv_ferror_rate', 'unknown_value', 'class']  # modified attack_Type and level and got only class column
        self.headers_to_check = ['Protocol_type', 'Service', 'Flag', 'Attack_type']
        self.df = None
        self.protocol_type = {'tcp': 1,'udp': 2,'icmp':3}
        self.flag = { 'OTH':1,'REJ':2,'RSTO':3,'RSTOS0':4,'RSTR':5,'S0':6,'S1':7,'S2':8,'S3':9,'SF':10,'SH':11}
        self.service = {'aol':1,'auth':2,'bgp':3,'courier':4,'csnet_ns':5,'ctf':6,'daytime':7,'discard':8,'domain':9,'domain_u':10,'echo':11,'eco_i':12,'ecr_i':13,'efs':14,'exec':15,'finger':16,'ftp':17,'ftp_data':18,'gopher':19,'harvest':20,'hostnames':21,'http':22,'http_2784':23,'http_443':24,'http_8001':25,'imap4':26,'IRC':27,'iso_tsap':28,'klogin':29,'kshell':30,'ldap':31,'link':32,'login':33,'mtp':34,'name':35,'netbios_dgm':36,'netbios_ns':37,'netbios_ssn':38,'netstat':39,'nnsp':40,'nntp':41,'ntp_u':42,'other':43,'pm_dump':44,'pop_2':45,'pop_3':46,'printer':47,'private':48,'red_i':49,'remote_job':50,'rje':51,'shell':52,'smtp':53,'sql_net':54,'ssh':55,'sunrpc':56,'supdup':57,'systat':58,'telnet':59,'tftp_u':60,'tim_i':61,'time':62,'urh_i':63,'urp_i':64,'uucp':65,'uucp_path':66,'vmnet':67,'whois':68,'X11':69,'Z39_50':70}

    def add_needed_columns(self):
        unique_values_list = []
        df = pd.read_csv(self.file_path, sep=',', names=self.header)
        columns_to_add = [None]
        for columns in df:
            print(columns)
            print(type(columns))
        print(df.head())
        unique_values_list.append(df['Service'].unique().tolist())
        print(df.Protocol_type.unique().tolist())
        print(unique_values_list)

    def get_df_with_headers(self):
        df = pd.read_csv(self.file_path, sep=',', names=self.header)
        return df

    def add_new_columns(self):
        self.df = self.get_df_with_headers(self)
        unique_values_to_include = []
        for column in self.headers_to_check:
            unique_values_to_include.append(self.df[column].unique().tolist())
        print(unique_values_to_include)
        for index, column_list in enumerate(unique_values_to_include):
            original_column = self.headers_to_check[index]
            for new_column in column_list:
                self.df[new_column] = self.df[original_column].apply(lambda x: 1 if x == new_column else 0)
        # print(self.df.info())
        # for col in self.df.columns:
        #     print(col)
        # print(self.df.head())

    def drop_old_columns(self):
        pass
        # TODO
        # get self.columns_to_check and remove them from the df
        self.df.drop(labels=self.headers_to_check, axis=1, inplace=True)
        print(self.df.info())

    @staticmethod
    def export_updated_input_file(df):
        df.to_csv("../../input/KDDTrainCleaned.csv", sep=',') # csv or txt, not sure yet
        # TODO
        # not sure if headers must be inside the output file or not

    def naives_bayes_algo(self, df):
        X = df.iloc[:, 0:41].values
        y = df.iloc[:, 41].values

        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2)
        classifier = GaussianNB(priors=None, var_smoothing=1e-09)
        clf = classifier.fit(X_train, y_train)
        pred = clf.predict(X_test)
        cm = confusion_matrix(y_test, pred)
        print(cm)

    def update_values_inside_df(self, df):
        df.Protocol_type = [self.protocol_type[item] for item in df.Protocol_type]
        df.Flag = [self.flag[item] for item in df.Flag]
        df.Service = [self.service[item] for item in df.Service]
        return df


if __name__ == '__main__':
    modify = ModifyInputFile('../../../input/KDDTrain+.csv')
    # modify.add_new_columns()
    # modify.drop_old_columns()
    df = modify.get_df_with_headers()
    df_modified = modify.update_values_inside_df(df)
    print(df_modified.head())
    modify.naives_bayes_algo(df_modified)
