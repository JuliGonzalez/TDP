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
                        'Dst_host_srv_ferror_rate', 'unknown_value', 'type', 'level']

    def add_needed_columns(self):
        df = pd.read_csv(self.file_path, sep=',', names=self.header)
        columns_to_add = ['']
        for columns in df:
            print(columns)
            print(type(columns))
        print(df.head())

print("hello world")
if __name__ == '__main__':
    modify = ModifyInputFile('../../../input/KDDTrain.csv')
    modify.add_needed_columns()
