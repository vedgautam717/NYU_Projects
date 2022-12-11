def twos_comp(val, bits):
    """compute the 2's complement of int value val"""
    if (val & (1 << (bits - 1))) != 0:
        val = val - (1 << bits)
    return val


def generate_bitstring(s):
    #TODO: rm, this is for debugging the error
    if '-' in s:
        return s
    return s[-1: -33: -1][::-1]


def twos_comp_str(s):
    s = list(s)
    for i in range(len(s)):
        if s[i] == '0':
            s[i] = '1'
        else:
            s[i] = '0'
    return '{:032b}'.format(int(''.join(s), 2) + 1)


def instruction_fetch(curr_state, next_state, instructions):

    if curr_state.IF["branch"] == 0:
        if (curr_state.IF["PC"] != next_state.IF["PC"] or curr_state.IF["PC"]==0):
            next_state.ID["Instr"] = instructions.readInstr(int(curr_state.IF["PC"]))
            next_state.ID["PC"] = curr_state.IF["PC"]
            if next_state.ID["Instr"][-7:] == "1111111":
                next_state.ID["nop"] = True
                next_state.IF["nop"] = True
        else:
            next_state.ID["Instr"] = curr_state.ID["Instr"]


def r_type(curr_state, next_state, register_file, memory):

    rd = curr_state.ID["Instr"][-12:-7]
    func3 = curr_state.ID["Instr"][-15:-12]
    rs1 = curr_state.ID["Instr"][-20:-15]
    rs2 = curr_state.ID["Instr"][-25:-20]
    func7 = curr_state.ID["Instr"][:-25]

    #-------------------------------Forwarding logic starts-----------------------------------
    # Forward for all instructions except for load value
    if rs1 == next_state.WB["DestReg"] and next_state.WB["WBEnable"]:
        # print("Forwarding from MEM Stage")
        next_state.EX["Operand1"] = next_state.WB["Wrt_data"]

    elif rs1 == next_state.MEM["DestReg"] and (next_state.MEM["WrDMem"] == 1 or next_state.MEM["RdDMem"] != 1):
        # print("Forwarding from EX Stage")
        next_state.EX["Operand1"] = next_state.MEM["ALUresult"]

    elif rs1 == curr_state.EX["DestReg"] and curr_state.EX["RdDMem"]:
        next_state.IF["PC"] = curr_state.IF["PC"]
        return

    else:
        next_state.EX["Operand1"] = register_file.readRF(rs1)


    if rs2 == next_state.WB["DestReg"] and next_state.WB["WBEnable"]:
        # print("Forwarding from MEM Stage")
        next_state.EX["Operand2"] = next_state.WB["Wrt_data"]

    elif rs2 == next_state.MEM["DestReg"] and (next_state.MEM["WrDMem"] == 1 or next_state.MEM["RdDMem"] != 1):
        # print("Forwarding from EX Stage")
        next_state.EX["Operand2"] = next_state.MEM["ALUresult"]

    elif rs2 == curr_state.EX["DestReg"] and curr_state.EX["RdDMem"]:
        next_state.IF["PC"] = curr_state.IF["PC"]
        return

    else:
        next_state.EX["Operand2"] = register_file.readRF(rs2)

    #-------------------------------Forwarding logic ends-----------------------------------

    next_state.EX["DestReg"] = rd

    if func3 == "000" and func7 == "0100000":
        # print("SUB")
        next_state.EX["AluControlInput"] = "0110"
    elif func3 == "000" and func7 == "0000000":
        # print("Add")
        next_state.EX["AluControlInput"] = "0010"
    elif func3 == "100" and func7 == "0000000":
        # print("XOR")
        next_state.EX["AluControlInput"] = "0011"
    elif func3 == "110" and func7 == "0000000":
        # print("OR")
        next_state.EX["AluControlInput"] = "0001"
    elif func3 == "111" and func7 == "0000000":
        # print("AND")
        next_state.EX["AluControlInput"] = "0000"

    next_state.EX["mux_out1"] = next_state.EX["Operand1"]
    next_state.EX["mux_out2"] = next_state.EX["Operand2"]
    next_state.EX["RdDMem"] = 0
    next_state.EX["WrDMem"] = 0
    next_state.EX["WBEnable"] = 1

    # Setting PC
    next_state.IF["PC"] = curr_state.IF["PC"] + 4


def i_type(curr_state, next_state, register_file, memory):
    opcode = curr_state.ID["Instr"][-7:]
    rd = curr_state.ID["Instr"][-12:-7]
    func3 = curr_state.ID["Instr"][-15:-12]
    rs1 = curr_state.ID["Instr"][-20:-15]
    imm = curr_state.ID["Instr"][:-20]

    #-------------------------------Forwarding logic starts-----------------------------------
    # Forward for all instructions except for load value
    if rs1 == next_state.WB["DestReg"] and next_state.WB["WBEnable"]:
        # print("Forwarding from MEM Stage")
        next_state.EX["Operand1"] = next_state.WB["Wrt_data"]

    elif rs1 == next_state.MEM["DestReg"] and (next_state.MEM["WrDMem"] == 1 or next_state.MEM["RdDMem"] != 1):
        # print("Forwarding from EX Stage")
        next_state.EX["Operand1"] = next_state.MEM["ALUresult"]

    elif rs1 == curr_state.EX["DestReg"] and curr_state.MEM["RdDMem"]:
        next_state.IF["PC"] = curr_state.IF["PC"]
        return

    else:
        next_state.EX["Operand1"] = register_file.readRF(rs1)

    #-------------------------------Forwarding logic ends-----------------------------------

    next_state.EX["Imm"] = twos_comp(int(imm,2), 12)
    next_state.EX["DestReg"] = rd
    next_state.EX["is_I_type"] = 1
    next_state.EX["AluOperation"] = "00"
    next_state.EX["mux_out1"] = next_state.EX["Operand1"]

    next_state.EX["RdDMem"] = 0
    next_state.EX["WrDMem"] = 0
    next_state.EX["WBEnable"] = 1

    if opcode == "0000011":
        next_state.EX["RdDMem"] = 1

    # ixtending imm to 32 bits
    diff_len = 32 - len(imm)
    next_state.EX["mux_out2"] = imm[0]*diff_len + imm

    if opcode == "0000011":
        # print("Load")
        next_state.EX["AluControlInput"] = "0010"
    elif func3 == "000":
        # print("ADDI")
        next_state.EX["AluControlInput"] = "0010"
    elif func3 == "100":
        # print("XORI")
        next_state.EX["AluControlInput"] = "0011"
    elif func3 == "110":
        # print("ORI")
        next_state.EX["AluControlInput"] = "0001"
    elif func3 == "111":
        # print("ANDI")
        next_state.EX["AluControlInput"] = "0000"

    # Setting PC
    next_state.IF["PC"] = curr_state.IF["PC"] + 4


def s_type(curr_state, next_state, register_file, memory):

    imm1 = curr_state.ID["Instr"][-12:-7]
    rs1 = curr_state.ID["Instr"][-20:-15]
    rs2 = curr_state.ID["Instr"][-25:-20]
    imm2 = curr_state.ID["Instr"][:-25]
    imm = imm2 + imm1

    #-------------------------------Forwarding logic starts-----------------------------------
    # Forward for all instructions except for load value
    if rs1 == next_state.WB["DestReg"] and next_state.WB["WBEnable"]:
        # print("Forwarding from MEM Stage")
        next_state.EX["Operand1"] = next_state.WB["Wrt_data"]

    elif rs1 == next_state.MEM["DestReg"] and (next_state.MEM["WrDMem"] == 1 or next_state.MEM["RdDMem"] != 1):
        # print("Forwarding from EX Stage")
        next_state.EX["Operand1"] = next_state.MEM["ALUresult"]

    elif rs1 == curr_state.EX["DestReg"] and curr_state.MEM["RdDMem"]:
        next_state.IF["PC"] = curr_state.IF["PC"]
        return

    else:
        next_state.EX["Operand1"] = register_file.readRF(rs1)

    #-------------------------------Forwarding logic ends-----------------------------------

    next_state.EX["DestReg"] = rs2
    next_state.EX["is_I_type"] = 1

    next_state.EX["WBEnable"] = 0
    next_state.EX["RdDMem"] = 0
    next_state.EX["WrDMem"] = 1

    next_state.EX["AluControlInput"] = "0010"

    next_state.EX["mux_out1"] = next_state.EX["Operand1"]

    diff_len = 32 - len(imm)
    next_state.EX["mux_out2"] = imm[0]*diff_len + imm
    next_state.EX["Imm"] = next_state.EX["mux_out2"]

    # Setting PC
    next_state.IF["PC"] = curr_state.IF["PC"] + 4


def j_type(curr_state, next_state, register_file, memory):

    opcode = curr_state.ID["Instr"][-7:]
    rd = curr_state.ID["Instr"][-12:-7]
    imm = curr_state.ID["Instr"][0] + curr_state.ID["Instr"][12:20] + curr_state.ID["Instr"][11:12] + curr_state.ID["Instr"][1:11]

    next_state.EX["Imm"] = imm

    curr_state.IF["branch"] = 1
    next_state.IF["jump"] = 1

    next_state.EX["DestReg"] = rd
    next_state.EX["RdDMem"] = 0
    next_state.EX["WrDMem"] = 0
    next_state.EX["WBEnable"] = 1

    # jump Logic
    # print("JAL")
    next_state.EX["Operand1"] = generate_bitstring('{:032b}'.format(curr_state.ID["PC"]))
    next_state.EX["Operand2"] = generate_bitstring('{:032b}'.format(4))

    next_state.EX["mux_out1"] = next_state.EX["Operand1"]
    next_state.EX["mux_out2"] = next_state.EX["Operand2"]
    next_state.EX["AluControlInput"] = "0010"

    imm = [
        (int(next_state.EX["Imm"], 2) << 1),
        (int(twos_comp_str(next_state.EX["Imm"]), 2) << 1) * -1
    ][next_state.EX["Imm"][0] == '1']
    next_state.IF["PC"] = curr_state.ID["PC"] + imm


def b_type(curr_state, next_state, register_file, memory):

    imm = curr_state.ID["Instr"][0] + curr_state.ID["Instr"][-8] + curr_state.ID["Instr"][1:-25] + curr_state.ID["Instr"][-12:-8]
    opcode = curr_state.ID["Instr"][-7:]
    func3 = curr_state.ID["Instr"][-15:-12]
    rs1 = curr_state.ID["Instr"][-20:-15]
    rs2 = curr_state.ID["Instr"][-25:-20]

    #-------------------------------Forwarding logic starts-----------------------------------
    # Forward for all instructions except for load value
    if rs1 == next_state.WB["DestReg"] and next_state.WB["WBEnable"]:
        # print("Forwarding from MEM Stage")
        next_state.EX["Operand1"] = next_state.WB["Wrt_data"]

    elif rs1 == next_state.MEM["DestReg"] and (next_state.MEM["WrDMem"] == 1 or next_state.MEM["RdDMem"] != 1):
        # print("Forwarding from EX Stage")
        next_state.EX["Operand1"] = next_state.MEM["ALUresult"]

    elif rs1 == curr_state.EX["DestReg"] and curr_state.EX["RdDMem"]:
        next_state.IF["PC"] = curr_state.IF["PC"]
        return

    else:
        next_state.EX["Operand1"] = register_file.readRF(rs1)


    if rs2 == next_state.WB["DestReg"] and next_state.WB["WBEnable"]:
        # print("Forwarding from MEM Stage")
        next_state.EX["Operand2"] = next_state.WB["Wrt_data"]

    elif rs2 == next_state.MEM["DestReg"] and (next_state.MEM["WrDMem"] == 1 or next_state.MEM["RdDMem"] != 1):
        # print("Forwarding from EX Stage")
        next_state.EX["Operand2"] = next_state.MEM["ALUresult"]

    elif rs2 == curr_state.EX["DestReg"] and curr_state.EX["RdDMem"]:
        next_state.IF["PC"] = curr_state.IF["PC"]
        return

    else:
        next_state.EX["Operand2"] = register_file.readRF(rs2)

    #-------------------------------Forwarding logic ends-----------------------------------

    # next_state.IF["branch"] = 1
    # next_state.IF["jump"] = 0

    next_state.EX["Imm"] = imm
    next_state.EX["mux_out1"] = next_state.EX["Operand1"]
    next_state.EX["mux_out2"] = next_state.EX["Operand2"]
    # next_state.EX["AluOperation"] = "00"
    AluControlInput = curr_state.ID["Instr"][17:20]


    # Branching Logic
    if AluControlInput == "000":
        # print("BEQ")
        if next_state.EX["mux_out1"] == next_state.EX["mux_out2"]:
            imm = [
                (int(next_state.EX["Imm"], 2) << 1),
                (int(twos_comp_str(next_state.EX["Imm"]), 2) << 1) * -1
            ][next_state.EX["Imm"][0] == '1']

            curr_pc = curr_state.ID["PC"] + imm
            next_state.IF["PC"] = curr_pc
            curr_state.IF["branch"] = 1
            next_state.IF["jump"] = 1
        else:
            next_state.IF["PC"] = curr_state.IF["PC"] + 4
    elif AluControlInput == "001":
        # print("BNE")
        if next_state.EX["mux_out1"] != next_state.EX["mux_out2"]:
            imm = [
                (int(next_state.EX["Imm"], 2) << 1),
                (int(twos_comp_str(next_state.EX["Imm"]), 2) << 1) * -1
            ][next_state.EX["Imm"][0] == '1']
            curr_pc = curr_state.ID["PC"] + imm
            next_state.IF["PC"] = curr_pc
            curr_state.IF["branch"] = 1
            next_state.IF["jump"] = 1
        else:
            next_state.IF["PC"] = curr_state.IF["PC"] + 4


def instruction_decode(curr_state, next_state, register_file, memory):
    # R Type 0110011
    if curr_state.ID["Instr"]:
        if curr_state.ID["Instr"][-7:] == "0110011":
            # print("Do R type")
            r_type(curr_state, next_state, register_file, memory)
            next_state.EX["nop"] = False

        # I Type 0010011 (LW 0000011)
        elif (curr_state.ID["Instr"][-7:] == "0010011") or (curr_state.ID["Instr"][-7:] == "0000011"):
            # print("Do I type")
            i_type(curr_state, next_state, register_file, memory)
            next_state.EX["nop"] = False

        # S Type 0100011
        elif curr_state.ID["Instr"][-7:] == "0100011":
            # print("Do S type")
            s_type(curr_state, next_state, register_file, memory)
            next_state.EX["nop"] = False

        # J Type 1101111
        elif curr_state.ID["Instr"][-7:] == "1101111":
            # print("Do J type")
            j_type(curr_state, next_state, register_file, memory)

        # B Type 1100011
        elif curr_state.ID["Instr"][-7:] == "1100011":
            # print("Do B type")
            b_type(curr_state, next_state, register_file, memory)

        # Halt 1111111
        elif curr_state.ID["Instr"][-7:] == "1111111":
            # print("Halt detected")
            next_state.EX["nop"] = True
            next_state.ID["nop"] = True
            next_state.IF["nop"] = True
            next_state.IF["PC"] = curr_state.IF["PC"]
        else:
            next_state.IF["PC"] = curr_state.IF["PC"] + 4
        next_state.EX["PC"] = curr_state.ID["PC"]

    else:
        # if curr_state.IF["branch"] or curr_state.IF["jump"]:
        #     next_state.IF["PC"] = curr_state.IF["PC"] + 4
        next_state.EX["nop"] = True


def instruction_exec(curr_state, next_state, register_file, memory):
    # carry over instructions
    next_state.MEM["DestReg"] = curr_state.EX["DestReg"]
    next_state.MEM["WBEnable"] = curr_state.EX["WBEnable"]
    next_state.MEM["RdDMem"] = curr_state.EX["RdDMem"]
    next_state.MEM["WrDMem"] = curr_state.EX["WrDMem"]

    next_state.EX["nop"] = curr_state.EX["nop"]
    next_state.ID["nop"] = curr_state.ID["nop"]
    next_state.IF["nop"] = curr_state.IF["nop"]

    next_state.MEM["nop"] = curr_state.IF["nop"]
    # if not curr_state.EX['branch']:

    if curr_state.EX["AluControlInput"] == "0110":
        # print("SUB")
        next_state.MEM["ALUresult"] = generate_bitstring('{:032b}'.format(int(curr_state.EX["mux_out1"],2) + int(twos_comp_str(curr_state.EX["mux_out2"]), 2)))
        next_state.MEM["nop"] = False

    elif curr_state.EX["AluControlInput"] == "0010":
        # print("ADD")
        # print(int(op1,2) + int(op2,2), '{0:032b}'.format(int(op1,2) + int(op2,2)))
        next_state.MEM["ALUresult"] = generate_bitstring('{:032b}'.format(int(curr_state.EX["mux_out1"],2) + int(curr_state.EX["mux_out2"],2)))
        next_state.MEM["nop"] = False

    elif curr_state.EX["AluControlInput"] == "0000":
        # print("AND")
        # Initialize res as NULL string
        res = ""
        for i in range(len(curr_state.EX["mux_out1"])):
            res = res + str(int(curr_state.EX["mux_out1"][i]) & int(curr_state.EX["mux_out2"][i]))
        # print(res)
        next_state.MEM["ALUresult"] = generate_bitstring('{:032b}'.format(int(res,2)))
        next_state.MEM["nop"] = False

    elif curr_state.EX["AluControlInput"] == "0001":
        # print("OR")
        res = ""
        for i in range(len(curr_state.EX["mux_out1"])):
            res = res + str(int(curr_state.EX["mux_out1"][i]) or int(curr_state.EX["mux_out2"][i]))

        next_state.MEM["ALUresult"] = generate_bitstring('{:032b}'.format(int(res,2)))
        next_state.MEM["nop"] = False

    elif curr_state.EX["AluControlInput"] == "0011":
        # print("XOR")
        next_state.MEM["ALUresult"] = generate_bitstring('{:032b}'.format(int(curr_state.EX["mux_out1"],2) ^ int(curr_state.EX["mux_out2"],2)))
        next_state.MEM["nop"] = False

    else:
        next_state.MEM["nop"] = True


def instruction_mem(curr_state, next_state, register_file, memory):
    # carry over instructions
    next_state.EX["nop"] = curr_state.EX["nop"]
    next_state.ID["nop"] = curr_state.ID["nop"]
    next_state.IF["nop"] = curr_state.IF["nop"]
    next_state.MEM["nop"] = curr_state.MEM["nop"]

    next_state.WB["nop"] = curr_state.MEM["nop"]
    # print(RdDMem, WrDMem, WBEnable)
    if curr_state.MEM["WrDMem"] == 1:
        # print("Write to memory", curr_state.MEM["ALUresult"], curr_state.MEM["DestReg"])
        # write in memory location of ALUresult
        # write data stored in DestReg
        # writeDataMem(self, Address, WriteData)
        # ALUresult = rs1 + imem
        memory.writeDataMem(curr_state.MEM["ALUresult"], register_file.readRF(curr_state.MEM["DestReg"]))
        next_state.WB["WBEnable"] = 0
    elif curr_state.MEM["RdDMem"] or curr_state.MEM["WBEnable"]:
        if curr_state.MEM["RdDMem"] == 1:
            # print("Read From memory")
            next_state.WB["Wrt_data"] = memory.readMem(int(curr_state.MEM["ALUresult"], 2))
        else:
            # print("AlU result will be written to memory")
            next_state.WB["Wrt_data"] = curr_state.MEM["ALUresult"]

        next_state.WB["WBEnable"] = 1
        next_state.WB["DestReg"] = curr_state.MEM["DestReg"]
    else:
        next_state.WB["nop"] = True
    next_state.WB["PC"] = curr_state.MEM["PC"]


def write_back(curr_state, next_state, register_file):
    # carry over instructions
    next_state.EX["nop"] = curr_state.EX["nop"]
    next_state.ID["nop"] = curr_state.ID["nop"]
    next_state.IF["nop"] = curr_state.IF["nop"]
    next_state.MEM["nop"] = curr_state.MEM["nop"]
    next_state.WB["nop"] = curr_state.WB["nop"]

    if curr_state.WB["WBEnable"]:
        # print(curr_state.WB["DestReg"], curr_state.WB["Wrt_data"])

        register_file.writeRF(curr_state.WB["DestReg"], curr_state.WB["Wrt_data"])
