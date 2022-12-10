import filecmp
import shutil
import os
import time
from termcolor import colored


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
    dir_path = os.path.dirname(__file__)
    total_test_cases = os.listdir(f'{dir_path}/TC')
    
    for tc in total_test_cases:
        print(f'Running testcase: {tc}')
        os.system(f'python3 {dir_path}/NYU_RV32I_6913.py --iodir={dir_path}/TC/{tc}')
        time.sleep(5) # Sleep for 5 seconds

        fs_dmem = file_compare(
            f"{dir_path}/TC/{tc}/ExpectedResults/FS_DMEMResult.txt",
            f"{dir_path}/TC/{tc}/FS_DMEMResult.txt"
        )
        fs_rf = file_compare(
            f"{dir_path}/TC/{tc}/ExpectedResults/FS_RFResult.txt",
            f"{dir_path}/TC/{tc}/FS_RFResult.txt"
        )
        ss_dmem = file_compare(
            f"{dir_path}/TC/{tc}/ExpectedResults/SS_DMEMResult.txt",
            f"{dir_path}/TC/{tc}/SS_DMEMResult.txt"
        )
        ss_rf = file_compare(
            f"{dir_path}/TC/{tc}/ExpectedResults/SS_RFResult.txt",
            f"{dir_path}/TC/{tc}/SS_RFResult.txt"
        )

        if fs_dmem and fs_rf and ss_dmem and ss_rf:
            print(colored(f'Test Case {tc} Passes', 'green'))
        else:
            print(colored(f'Test Case {tc} Failed', 'red'))

            for i in range(3):
                print("stop at this test case")
                print(3-i)
                time.sleep(1)
