#!/usr/bin/env python3
import json
import pandas as pd
import tkinter as tk
from tkinter import filedialog

def flat_keys(obj, new_obj={}, keys=[]):
    for key, value in obj.items():
        if isinstance(value, dict):
            flat_keys(obj[key], new_obj, keys + [key])
        else:
            new_obj['.'.join(keys + [key])] = value
    return new_obj

root = tk.Tk()
root.withdraw()

file_path = filedialog.askopenfilename()

with open(file_path, 'r') as f:
    datastore = json.load(f)
    new_obj = flat_keys(datastore)
    df = pd.DataFrame(new_obj, index=['value'])
    transposed = df.T
    print('Excel created correctly!')
    transposed.to_excel('translations.xls')

