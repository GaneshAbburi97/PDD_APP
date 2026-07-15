import pandas as pd
import os

# Define paths
base_dir = os.path.dirname(os.path.abspath(__file__))
csv_path = os.path.join(base_dir, 'Dentzy_TMD_Test_Results.csv')
excel_path = os.path.join(base_dir, 'reports', 'appium_tests', 'Dentzy_TMD_Test_Results.xlsx')

# Read CSV and save as true Excel XLSX
df = pd.read_csv(csv_path)
df.to_excel(excel_path, index=False, engine='openpyxl')

# Clean up the old CSV file and scripts to keep the project clean
if os.path.exists(csv_path):
    os.remove(csv_path)
gen_script = os.path.join(base_dir, 'generate_tests.py')
if os.path.exists(gen_script):
    os.remove(gen_script)
print(f"Successfully converted to Excel and saved at {excel_path}")
