import filecmp
import shutil
import os
import time
from termcolor import colored

from difflib import Differ

def file_compare(file1, file2):
    file1 = open(file1,'r')
    file2 = open(file2,'r')
    file1_lines = file1.readlines()
    file2_lines = file2.readlines()

    for i in range(len(file1_lines)):
        try:
            if file1_lines[i] != file2_lines[i]:
                return False
        except:
            return False

    return True

if __name__ == "__main__":
    total_test_cases = range(5)
    dir_path = os.path.dirname(os.path.realpath(__file__))
    destination_imem = f"{dir_path}/imem.txt"
    destination_dmem = f"{dir_path}/dmem.txt"
    
    for tc in total_test_cases:
        # copy necessary test case files
        imem_loc = f"{dir_path}/TC{tc}/imem.txt"
        dmem_loc = f"{dir_path}/TC{tc}/dmem.txt"
        shutil.copyfile(imem_loc, destination_imem)
        shutil.copyfile(dmem_loc, destination_dmem)
        time.sleep(2) # Sleep for 2 seconds

        os.system('python3 NYU_RV32I_6913.py')
        time.sleep(5) # Sleep for 5 seconds

        fs_dmem = file_compare(f"{dir_path}/TC{tc}/ExpectedResults/FS_DMEMResult.txt",
                                f"{dir_path}/FS_DMEMResult.txt")
        fs_rf = file_compare(f"{dir_path}/TC{tc}/ExpectedResults/FS_RFResult.txt",
                                f'{dir_path}/FS_RFResult.txt')
        ss_dmem = file_compare(f"{dir_path}/TC{tc}/ExpectedResults/SS_DMEMResult.txt",
                                f'{dir_path}/SS_DMEMResult.txt')
        ss_rf = file_compare(f"/Users/vedgautam/Desktop/Fall_2022/project_csa/TC{tc}/ExpectedResults/SS_RFResult.txt",
                                f'{dir_path}/SS_RFResult.txt')

        if fs_dmem and fs_rf and ss_dmem and ss_rf:
            print(colored(f'Test Case {tc} Passes', 'green'))
        else:
            print(colored(f'Test Case {tc} Failed', 'red'))

            for i in range(3):
                print("stop at this test case")
                print(3-i)
                time.sleep(1)
