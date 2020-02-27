#!/usr/bin/
# -*- coding: utf-8 -*-
"""
Script designed to change the structure of the input file to match the
k-means algorithm criteria
"""
import pandas as pd


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
                        'Dst_host_srv_ferror_rate', 'unknown_value', 'Attack_type', 'level']
        self.headers_to_check = ['Protocol_type', 'Service', 'Flag', 'Attack_type']
        self.df = None

    def add_needed_columns(self):
        unique_values_list = []
        df = pd.read_csv(self.file_path, sep=',', names=self.header)
        columns_to_add = ['']
        for columns in df:
            print(columns)
            print(type(columns))
        print(df.head())
        unique_values_list.append(df['Service'].unique().tolist())
        print(df.Protocol_type.unique().tolist())
        print(unique_values_list)

    @staticmethod
    def set_headers(self):
        df = pd.read_csv(self.file_path, sep=',', names=self.header)
        return df

    def add_new_columns(self):
        self.df = self.set_headers(self)
        unique_values_to_include = []
        for column in self.headers_to_check:
            unique_values_to_include.append(self.df[column].unique().tolist())
        print(unique_values_to_include)
        for index, column_list in enumerate(unique_values_to_include):
            original_column = self.headers_to_check[index]
            for new_column in column_list:
                self.df[new_column] = self.df[original_column].apply(lambda x: 1 if x == new_column else 0)
        print(self.df.info())
        for col in self.df.columns:
            print(col)
        print(self.df.head())


    def drop_old_columns(self):
        pass
        # TODO
        # get self.columns_to_check and remove them from the df
        self.df.drop(labels=self.headers_to_check, axis=1, inplace=True)
        print(self.df.info())


    def export_updated_input_file(self, df):
        df.to_csv("../../input/KDDTrainCleaned.csv", sep=',') # csv or txt, not sure yet
        # TODO
        # not sure if headers must be inside the output file or not


if __name__ == '__main__':
    modify = ModifyInputFile('../../../input/KDDTrain.csv')
    modify.add_new_columns()
    modify.drop_old_columns()
    