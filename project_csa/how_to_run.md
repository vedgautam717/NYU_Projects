# How to use this simulator

## Use simulator Method 1
1. Run `pip install -r requirements.txt`
2. Place your test cases in the folder TC
3. Run the python script checker.py `python3 checker.py`
>Note: Ensure that your are in the same current working dir as that of checker
4. For every test case the result files get generated within that test case folder

## File structure
```
- CWD where checker.py exists run the python command
    - NYU_RV32I_6913.py
    - checker.py
    - TC (list of all test case folder)
        - TC0 (0th test case)
            - Expected Results (where your final results should exist)
            - imem.txt (instruction mem file)
            - dmem.txt (data mem file)
            ~~~~~~~~~~~~Output files~~~~~~~~~~~~~~
            - FS_DMEMResult.txt
            - FS_RFResult.txt
            - SS_DMEMResult.txt
            - SS_RFResult.txt
            - StateResult_FS.txt
            - StateResult_SS.txt
```

## Use simulator Method 2
In this method we will discuss how to run the simulator for individual outputs and not run test cases
1. Run `pip install -r requirements.txt`
2. Place your `imem.txt` and `dmem.txt` in the dir with `NYU_RV32I_6913.py`
3. Run the python script `python3 NYU_RV32I_6913.py`
>Note: Ensure that your are in the same current working dir as that of NYU_RV32I_6913.py
4. Your results will get generated in the same working dir
