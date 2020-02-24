"""
Script designed to change the structure of the input file to match the
k-means algorithm criteria
"""
import pandas as pd


class ModifyInputFile(object):
    def __init__(self, file_path):
        self.file_path = file_path

    def get_headers(self):
        pass

    def add_new_columns(self, headers_to_include):
        pass

    def drop_old_columns(self, headers_to_remove):
        pass

    def export_updated_input_file(self, df):
        df.to_csv("../../input/KDDTrainCleaned.csv", sep=',') # csv or txt, not sure yet



print("hello world")
