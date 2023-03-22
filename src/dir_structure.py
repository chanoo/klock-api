# python dir_structure.py
import os

def list_full_package_names(start_path):
    for root, dirs, files in os.walk(start_path):
        if '.git' not in root:  # Exclude .git directory if present
            package_name = root.replace(start_path, '').replace(os.sep, '.').strip('.')
            print(package_name)

if __name__ == '__main__':
    project_path = input('Enter the path to the project root directory: ')
    list_full_package_names(project_path)
