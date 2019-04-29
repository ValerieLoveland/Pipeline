public class Pipeline {

    static int[] arr = new int[]{0x00000000, 0x00000000, 0x00000000, 0x00000000,
            0x00000000, 0x00000000, 0x00000000,
            0xa1020000, 0x810AFFFC, 0x00831820, 0x01263820, 0x01224820, 0x81180000,
            0x81510010, 0x00624022, 0x00000000, 0x00000000, 0x00000000, 0x00000000 , 0x00000000, 0x00000000};

    static int[] Main_Mem = new int[0x400];
    static int[] Regs = new int[32];

    static String sign = null;
    static int isrc1, isrc2, ifunct, ioffset;
    static int opcode;
    static int rsrc1, rsrc2, rfunct, rdes;
    static String Zero = "F";
    static String calcBTA = "X";
    static String RegDst, Function, RegWrite, LWDataValue, SEOffset, WriteRegNumber, MemToReg;
    static String equation;
    static int decodedInst;
    static int branch = 0;
    static int i = 0;
    static int hexnum;
    static int inst = hexnum;
    static int cycleNumber = 1;
    static int SWDataValue, ALUResult, ALUSrc, ALUOp, MemRead, MemWrite;
    static int ReadReg1Value, ReadReg2Value, WriteReg_20_16, WriteReg_15_11;

    public static void main(String[] args) {
        fillMain_Mem();
        fillRegs();
        ClockCycleZero();

        for (i = 0; i < 12; i++) {
            hexnum = arr[i];
            Print_out_everything();
        }
    }

    private static void fillMain_Mem() {
        int mmFillWholeList = 0x00;
        for (int repeatedFill = 0; repeatedFill <= 0x03; repeatedFill++) {
            for (int i = 0x00; i <= 0xff; i++) {
                Main_Mem[mmFillWholeList] = i;
                mmFillWholeList++;
//                System.out.printf("%x", Main_Mem[i]);
//                System.out.println();
            }
        }
    }

    private static void fillRegs() {
        int RegsStartsAt100 = 0x101;
//        System.out.println("Regs");
        for (int i = 1; i < 32; i++) {
            Regs[i] = RegsStartsAt100;
            RegsStartsAt100++;
//            System.out.printf("%x", Regs[i]);
//            System.out.println();
        }
    }

    public static void disassembler() {


        opcode = (hexnum >>> 26);
        if (opcode == 0) {
            opcodeZeroAssingingSigns();
            opcodeZero();
        }
        if (opcode == 0x20) {
            sign = "lw";
            lwSw();
        }

        if (opcode == 0x28) {
            sign = "sw";
            lwSw();
        }
    }

    public static void IF_stage() {
        hexnum = arr[cycleNumber + 6];
        disassembler();
        inst = arr[cycleNumber + 8];
    }

    public static void ID_stage() {
        hexnum = arr[cycleNumber + 5];
        disassembler();

        //IF_stage();
        hexnum = arr[cycleNumber + 5];
        disassembler();
        inst = arr[cycleNumber + 7];

        RegDst();
        ALUSrc();
        ALUOp();
        MemRead();
        MemWrite();
        branch = 0;
        MemToReg();
        RegWrite();
        Function();
        SEOffset();
        writeReg_20_16();
        ReadReg1Value();
        ReadReg2Value();
    }

    public static void EX_stage() {
        hexnum = arr[cycleNumber + 4];

        disassembler();
        inst = arr[cycleNumber + 6];
        //disassembler();
        //ID_stage();
        RegDst();
        ALUSrc();
        ALUOp();
        MemRead();
        MemWrite();
        branch = 0;
        Function();
        SEOffset();
        writeReg_20_16();
        MemToReg();
        ReadReg1Value();
        ReadReg2Value();
        RegWrite();
        calcBTA = "X";
        Zero = "F";
        ALUResult();
        WriteRegNumber();
        SWDataValue();

    }

    public static void MEM_stage() {
        hexnum = arr[cycleNumber + 3];
        disassembler();
        inst = arr[cycleNumber + 5];
        //EX_stage();
        RegDst();
        ALUSrc();
        ALUOp();
        MemRead();
        MemWrite();
        branch = 0;
        Function();
        SEOffset();
        writeReg_20_16();
        ReadReg1Value();
        ReadReg2Value();
        calcBTA = "X";
        Zero = "F";
        WriteRegNumber();
        SWDataValue();


        MemToReg();
        RegWrite();
        ALUResult();
        WriteRegNumber();
        LWDataValue();
    }

    public static void WB_stage() {
        hexnum = arr[cycleNumber + 2];
        disassembler();
        inst = arr[cycleNumber + 4];
        //MEM_stage();
        MemToReg();
        RegWrite();
        ALUResult();
        WriteRegNumber();
        LWDataValue();
        //WRITE WHAT PRINTS OUT TO SAY THE NEW VALUE


    }

    public static void opcodeZeroAssingingSigns() {
        rsrc1 = (hexnum & 0x03e00000) >> 21;
        rsrc2 = (hexnum & 0x1f0000) >> 16;
        rdes = (hexnum & 0xf800) >>> 11;
        rfunct = (hexnum & 0x3f);

        if (rfunct == 0x20) {
            sign = "add";
        }

        if (rfunct == 0x22) {
            sign = "sub";
        }
    }

    public static void opcodeZero() {

        equation = (sign + " $" + rdes + ", " + "$" + rsrc1 + ", " + "$" + rsrc2).toString();
        // address = address + 0x04;

    }


    public static void lwSw() {
        isrc1 = (hexnum & 0x3e00000) >>> 21;
        isrc2 = (hexnum & 0x1f0000) >>> 16;
        ioffset = (byte) (hexnum & 0xffff);
        //System.out.printf("0x%02X", address);
        //System.out.print(sign + " $" + isrc2 + ", " + ioffset + "(" + "$" + isrc1 + ")");
        equation = sign + " $" + isrc2 + ", " + ioffset + "(" + "$" + isrc1 + ")".toString();
        //  address = address + 0x04;

    }


    public static void Print_out_everything() {
        hexnum = arr[cycleNumber + 6];

        disassembler();
        inst = arr[cycleNumber + 6];
        System.out.println();
        System.out.println("Clock Cycle " + cycleNumber + " (" + "Before we copy the write side of the pipeline registers to the read side" + ")");


        System.out.println();
        // System.out.println("IF/ID Read (Read by the ID stage)");
        hexnum = arr[cycleNumber + 7];
        disassembler();
        inst = hexnum;
        System.out.println();

        System.out.println();
        IF_stage();
        System.out.println("IF/ID Write (Written to by the IF stage)");
        // System.out.println("This is the IF version");
        System.out.print("Inst= ");
        if (arr[cycleNumber + 6] == 0x00000000) {
            System.out.println("0x00000000");
//        } else if (arr[cycleNumber + 6] == 0x00000000){
//            NOPIFIDWritePrint();
        } else {
            System.out.printf("0x%02X", arr[cycleNumber + 6]);
            System.out.print("        [");
            //disassembler();
            System.out.print(equation);
            System.out.print("]");
//            System.out.print("       IncrPC= ");
//            System.out.printf("0x%02X", address);
        }
        System.out.println();
        System.out.println();


        ID_stage();
        System.out.println("IF/ID Read (Read by the ID stage)");
        // System.out.println("This is the IF version");
        hexnum = arr[cycleNumber + 5];
        System.out.print("Inst= ");
        if (arr[cycleNumber + 5] == 0x00000000) {
            System.out.println("0x00000000");
//        } else if (arr[cycleNumber + 5] == 0x00000000){
//            NOPIFIDReadPrint();
        }else {
            IFIDReadPrint();
        }
        System.out.println();
        System.out.println();

        System.out.println("ID/EX Write (Written to by the ID stage)");
        System.out.print("Control = ");
        if (arr[cycleNumber + 5] == 0x00000000) {
            System.out.println("0x00000000");
//        } else if (arr[cycleNumber + 5] == 0x00000000){
//            NOPIDEXWritePrint();
        } else {
            IDEXWritePrint();
        }
        System.out.println();
        System.out.println();
        EX_stage();
        System.out.println("ID/EX Read (Read to by the EX stage)");
        hexnum = arr[cycleNumber + 4];
        System.out.print("Control = ");
        if (arr[cycleNumber + 4] == 0x00000000) {
            System.out.println("0x00000000");
//        } else if (arr[cycleNumber + 4] == 0x00000000){
//            NOPIDEXReadPrint();
        } else {
            IDEXReadPrint();
        }
        System.out.println();
        System.out.println();

        System.out.println("EX/MEM Write (Written to by the EX stage)");
        System.out.print("Control = ");
        if (arr[cycleNumber + 4] == 0x00000000) {
            System.out.println("0x00000000");
//        } else if (arr[cycleNumber + 4] == 0x00000000){
//            NOPEXMEMWritePrint();
        } else {
            EXMEMWritePrint();
        }
        System.out.println();
        System.out.println();

        MEM_stage();
        System.out.println("EX/MEM Read (Read by the MEM stage)");
        hexnum = arr[cycleNumber + 3];
        System.out.print("Control = ");
        if (arr[cycleNumber + 3] == 0x00000000) {
            System.out.println("0x00000000");
//        } else if (arr[cycleNumber + 3] == 0x00000000){
//            NOPEXMEMReadPrint();
        } else {
            EXMEMReadPrint();
        }
        System.out.println();
        System.out.println();

        System.out.println("MEM/WB Write (Written by the MEM stage)");
        System.out.print("Control = ");
        if (arr[cycleNumber + 3] == 0x00000000) {
            System.out.println("0x00000000");
//        } else if (arr[cycleNumber + 3] == 0x00000000){
//            NOPMEMWBWritePrint();
        } else {
            MEMWBWritePrint();
                    }
        if (opcode==0x28){
            MEMWBWritePrintForSW();
        }
        System.out.println();
        System.out.println();

        WB_stage();
        System.out.println("MEM/WB Read (Read by the WB stage)");
        System.out.print("Control = ");
        if (arr[cycleNumber + 2] == 0x00000000) {
            System.out.println("0x00000000");
        } else {
            MEMWBReadPrint();
            //WBReadActionprint();
            WBReadAction();
        }
        System.out.println();
        System.out.println();
        System.out.println();
       // hexnum = arr[cycleNumber + 6];

       // disassembler();
       // inst = arr[cycleNumber + 6];
       // System.out.println();



        cycleNumber++;

RegsPrint();

    }


    public static String RegDst() {
        disassembler();
        // System.out.print("opcode is ");
        // System.out.printf("0x%02X", opcode);
        System.out.println();
        if (opcode == 0x28) {
            RegDst = "X";
        } else if (opcode == 0x20) {
            RegDst = "0";
        } else {
            RegDst = "1";
        }
        return RegDst;
    }

    public static int ALUSrc() {
        disassembler();
        if (opcode == 0x0) {
            ALUSrc = 0;
        } else {
            ALUSrc = 1;

        }
        return ALUSrc;
    }

    public static int ALUOp() {
        disassembler();
        if (opcode == 0x0) {
            ALUOp = 10;
        } else {
            ALUOp = 00;

        }
        return ALUSrc;


    }

    public static int MemRead() {
        disassembler();
        if (opcode == 0x20) {
            MemRead = 1;
        } else {
            MemRead = 0;

        }
        return MemRead;
    }

    public static int MemWrite() {
        if (opcode == 0x28) {
            MemWrite = 1;
        } else {
            MemWrite = 0;

        }
        return MemWrite;
    }

    public static String MemToReg() {
        disassembler();
        if (opcode == 0x20) {
            MemToReg = "1";
        } else if (opcode == 0x28) {
            MemToReg = "X";

        } else {
            MemToReg = "0";
        }
        return MemToReg;
    }

    public static String RegWrite() {
        if (opcode == 0x28) {
            RegWrite = "0";
        } else {
            RegWrite = "1";
        }
        return RegWrite;
    }

    public static int ReadReg1Value() {

        disassembler();
        if (opcode == 0x0) {
            ReadReg1Value = 100 + rsrc1;

        } else {
            ReadReg1Value = 100 + isrc1;
        }
        return ReadReg2Value;
    }

    public static int ReadReg2Value() {
        disassembler();
        if (opcode == 0x0) {
            ReadReg2Value = 100 + rsrc2;

        } else {
            ReadReg2Value = 100 + isrc2;
        }
        return ReadReg2Value;
    }

    public static String SEOffset() {
        disassembler();
        if ((opcode == 0x20 || opcode == 0x28) && ioffset >= 0) {
            SEOffset = "0000000" + Integer.toHexString(ioffset);
        } else if ((opcode == 0x20 || opcode == 0x28) && ioffset < 0) {
            SEOffset = Integer.toHexString(ioffset);
        } else {
            SEOffset = "x";
        }
        return SEOffset;


    }

    public static int writeReg_20_16() {
        disassembler();
        if (opcode == 0x0) {
            WriteReg_20_16 = rsrc2;
        } else {
            WriteReg_20_16 = isrc2;

        }
        return WriteReg_20_16;
    }

    public static int WriteReg_15_11() {
        disassembler();
        if (opcode == 0x0) {
            WriteReg_15_11 = rdes;
        } else if(opcode==0x28){
            WriteReg_15_11 = 31;}
        else{
            WriteReg_15_11 =0;

        }
        return WriteReg_15_11;
    }

    public static String Function() {
        disassembler();
        // System.out.println("function is "+Function);
        if (rfunct == 0x20) {
            Function = "20";
        } else if (rfunct == 0x22) {
            Function = "22";

        } else {
            Function = "X";
        }
        return Function;
    }

    public static int ALUResult() {
        disassembler();
        // System.out.println("readreg1val= "+ ReadReg1Value+" ioffset= "+ ioffset);
        if (rfunct == 0x20) {
            ALUResult = ReadReg1Value + ReadReg2Value;
        } else if (rfunct == 0x22) {
            ALUResult = ReadReg1Value - ReadReg2Value;
        } else {
            if (ioffset >= 0) {
                ALUResult = ReadReg1Value + ioffset;
            } else {
                ALUResult = ReadReg1Value - ioffset;
            }
        }
        return ALUResult;
    }


    public static String WriteRegNumber() {
        disassembler();
        if (opcode == 0x20) {
            WriteRegNumber = Integer.toString(isrc2);
        } else if (opcode == 0x28) {
            WriteRegNumber = "X";

        } else {
            WriteRegNumber = Integer.toString(rdes);

        }
        return WriteRegNumber;
    }


    public static String LWDataValue() {
        disassembler();
        if (opcode == 0x20) {
            LWDataValue = Integer.toString(ALUResult);
        } else {
            LWDataValue = "X";
        }
        return LWDataValue;
    }

    public static int SWDataValue() {
        disassembler();
        if (opcode == 0x0) {
            SWDataValue = 100 + ReadReg2Value;
        } else {
            SWDataValue = 100 + isrc2;
        }
        return SWDataValue;
    }

    public static void WBReadAction() {
        disassembler();
        if (opcode == 0x0) {
            Regs[rdes]=ALUResult;
            System.out.println("$"+ rdes +" is set to new value of ");
            System.out.printf("0x%02X", ALUResult());
        } else if(opcode==0x20){
            Regs[isrc2]=Main_Mem[ALUResult];
            System.out.print("$"+isrc2+" is set to a new value of ");
            System.out.printf("0x%02X", Main_Mem[ALUResult]);
        }else{
            Main_Mem[ALUResult]=SWDataValue;
            System.out.println("No register is written to since a SW does not write to a register");
        }
    }


    public static void ClockCycleZero() {
        System.out.println("Clock Cycle 0 (before we start the specified instructions)");
        System.out.println("IF/ID Write (Written to by the IF stage)");
        System.out.println("Inst = 0x00000000");
        System.out.println();
        System.out.println("IF/ID Read (Read by the ID stage)");
        System.out.println("Inst = 0x00000000");
        System.out.println();
        System.out.println("ID/EX Write (Written to by the ID stage)");
        System.out.println("Control = 0x00000000");
        System.out.println();
        System.out.println("ID/EX Read (Read by  the EX stage)");
        System.out.println("Control = 0x00000000");
        System.out.println();
        System.out.println("EX/MEM Write (Written to by the EX stage)");
        System.out.println("Control = 0x00000000");
        System.out.println();
        System.out.println("EX/MEM Read (Read by  by the MEM stage)");
        System.out.println("Control = 0x00000000");
        System.out.println();
        System.out.println("MEM/WB Write (Written to by the MEM stage)");
        System.out.println("Control = 0x00000000");
        System.out.println();
        System.out.println("MEM/WB Write (Written to by the MEM stage)");
        System.out.println("Control = 0x00000000");
        System.out.println();
        System.out.println();
        System.out.println();
    }

    public static void IFIDReadPrint() {
        System.out.printf("0x%02X", arr[cycleNumber + 7]);
        System.out.print("        [");
        //disassembler();
        System.out.print(equation);
        System.out.print("]");
//        System.out.print("       IncrPC= ");
        // System.out.printf("0x%02X", address);
    }



    public static void IDEXWritePrint() {
        System.out.print("RegDst = " + RegDst + " ALUSrc= " + ALUSrc + " ALUOp= " + ALUOp + " MemRead= " + MemRead + " MemWrite= " + MemWrite);
        System.out.print("Branch = " + branch + " MemToReg = " + MemToReg + " RegWrite = " + RegWrite + " [ " + sign + " ]");
        System.out.println();
        System.out.println(" ReadReg1Value= " + ReadReg1Value + " ReadReg2Value= " + ReadReg2Value);
        System.out.print("SEOffset = " + SEOffset + " WriteReg_20_16= " + WriteReg_20_16 + " WriteReg_15_11= " + WriteReg_15_11 + " Function= " + Function);

    }

    public static void IDEXReadPrint() {
        System.out.print("RegDst = " + RegDst + " ALUSrc= " + ALUSrc + " ALUOp= " + ALUOp + " MemRead= " + MemRead + " MemWrite= " + MemWrite);
        System.out.print("Branch = " + branch + " MemToReg = " + MemToReg + " RegWrite = " + RegWrite + " [ " + sign + " ]");
        System.out.println();
        System.out.println(" ReadReg1Value= " + ReadReg1Value + " ReadReg2Value= " + ReadReg2Value);
        System.out.print("SEOffset = " + SEOffset + " WriteReg_20_16= " + WriteReg_20_16 + " WriteReg_15_11= " + WriteReg_15_11 + " Function= " + Function);

    }

    public static void EXMEMWritePrint() {
        System.out.print("MemRead = " + MemRead + " MemWrite= " + MemWrite + " Branch= " + branch + " MemToReg= " + MemToReg + " RegWrite= " + RegWrite + " [ " + sign + " ] ");
        System.out.print("CalcBTA = " + calcBTA + " Zero= " + Zero + " ALUResult = " + ALUResult);
        System.out.println();
        System.out.print("SWValue = " + SWDataValue + " WriteRegNum= " + WriteRegNumber);
    }

    public static void EXMEMReadPrint() {
        System.out.print("MemRead = " + MemRead + " MemWrite= " + MemWrite + " Branch= " + branch + " MemToReg= " + MemToReg + " RegWrite= " + RegWrite + " [ " + sign + " ] ");
        System.out.print("CalcBTA = " + calcBTA + " Zero= " + Zero + " ALUResult = " + ALUResult);
        System.out.println();
        System.out.print("SWValue = " + SWDataValue + " WriteRegNum= " + WriteRegNumber);
    }

    public static void MEMWBWritePrint() {
        System.out.print(" MemToReg = " + MemToReg + " RegWrite = " + RegWrite + " [ " + sign + " ]");
        System.out.println();
        System.out.println(" LWDataValue = " + LWDataValue + " ALUResult = " + ALUResult + " WriteRegNum= " + WriteRegNumber);

    }
public static void MEMWBWritePrintForSW(){
    System.out.println("Value "+SWDataValue+" is written to memory address " +Main_Mem[ALUResult]);

}
    public static void MEMWBReadPrint() {
        System.out.print(" MemToReg = " + MemToReg + " RegWrite = " + RegWrite + " [ " + sign + " ]");
        System.out.println();
        System.out.println(" LWDataValue = " + LWDataValue + " ALUResult = " + ALUResult + " WriteRegNum= " + WriteRegNumber);



        }
    public static void RegsPrint() {
        System.out.println("Printing registers:");
        for (int i = 1; i < 32; i++) {
            System.out.printf("%x", Regs[i]);
            System.out.println();
        }}



    public static void Copy_write_to_read() {
//        System.out.println("Clock Cycle " + cycleNumber + " (" + "After we copy the write side of the pipeline registers to the read side" + ")");
//
//
//        System.out.println();
//
        hexnum = arr[cycleNumber + 7];
        disassembler();
        inst = hexnum;
        //      System.out.println();

        //       System.out.println();
        //      IF_stage();
//        System.out.println("IF/ID Write (Written to by the IF stage)");
//        // System.out.println("This is the IF version");
//        System.out.print("Inst= ");
        //       if (arr[cycleNumber + 7] == 0x99999999) {
//            System.out.println("0x00000000");
//        } else if (arr[cycleNumber + 7] == 0x00000000){
//            NOPIFIDWritePrint();
//        } else {
//            System.out.printf("0x%02X", arr[cycleNumber + 7]);
//            System.out.print("        [");
//            //disassembler();
//            System.out.print(equation);
//            System.out.print("]");
//            System.out.print("       IncrPC= ");
//            System.out.printf("0x%02X", address);
//        }
//        System.out.println();
//        System.out.println();
//
//
//        //ID_stage();
//        System.out.println("IF/ID Read (Read by the ID stage)");
//        // System.out.println("This is the IF version");
//        System.out.print("Inst= ");
//        if (arr[cycleNumber + 7] == 0x99999999) {
//            System.out.println("0x00000000");
//        } else if (arr[cycleNumber + 7] == 0x00000000){
//            NOPIFIDReadPrint();
//        } else {
//            IFIDReadPrint();
//        }
//        System.out.println();
//        System.out.println();
        //       ID_stage();
//        System.out.println("ID/EX Write (Written to by the ID stage)");
//        System.out.print("Control = ");
//        if (arr[cycleNumber + 5] == 0x99999999) {
//            System.out.println("0x00000000");
//        } else if (arr[cycleNumber + 5] == 0x00000000){
//            NOPIDEXWritePrint();
//        } else {
//            IDEXWritePrint();
//        }
//        System.out.println();
//        System.out.println();
//
//        System.out.println("ID/EX Read (Read to by the EX stage)");
//        System.out.print("Control = ");
//        if (arr[cycleNumber + 5] == 0x99999999) {
//            System.out.println("0x00000000");
//        } else if (arr[cycleNumber + 5] == 0x00000000){
//            NOPIDEXReadPrint();
//        } else {
//            IDEXReadPrint();
//        }
//        System.out.println();
//        System.out.println();
//        EX_stage();
//        System.out.println("EX/MEM Write (Written to by the EX stage)");
//        System.out.print("Control = ");
//        if (arr[cycleNumber + 4] == 0x99999999) {
//            System.out.println("0x00000000");
//        } else if (arr[cycleNumber + 4] == 0x00000000){
//            NOPEXMEMWritePrint();
//        } else {
//            EXMEMWritePrint();
//        }
//        System.out.println();
//        System.out.println();
//
//
//        System.out.println("EX/MEM Read (Read by the MEM stage)");
//        System.out.print("Control = ");
//        if (arr[cycleNumber + 4] == 0x99999999) {
//            System.out.println("0x00000000");
//        } else if (arr[cycleNumber + 4] == 0x00000000){
//            NOPEXMEMReadPrint();
//        } else {
//            EXMEMReadPrint();
//        }
//        System.out.println();
//        System.out.println();
//        MEM_stage();
//        System.out.println("MEM/WB Write (Written by the MEM stage)");
//        System.out.print("Control = ");
//        if (arr[cycleNumber + 3] == 0x99999999) {
//            System.out.println("0x00000000");
//        } else if (arr[cycleNumber + 3] == 0x00000000){
//            NOPMEMWBWritePrint();
//        } else {
//            MEMWBWritePrint();
//
//        }
//        System.out.println();
//        System.out.println();
//        System.out.println("MEM/WB Read (Read by the WB stage)");
//        System.out.print("Control = ");
//        if (arr[cycleNumber + 3] == 0x99999999) {
//            System.out.println("0x00000000");
//        } else if (arr[cycleNumber + 3] == 0x00000000){
//            NOPMEMWBReadPrint();
//        } else {
//            MEMWBReadPrint();
//
//        }
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println();
//
//
    }
}
