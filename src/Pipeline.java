public class Pipeline {

    static int[] arr = new int[]{0xa1020000, 0x810AFFFC, 0x00831820, 0x01263820, 0x01224820, 0x81180000,
            0x81510010, 0x00624022, 0x00000000, 0x00000000, 0x00000000, 0x00000000};

    static int[] Main_Mem = new int[0x400];
    static int[] Regs = new int[32];
    static int hexnum;
    static String sign = null;
    static int isrc1, isrc2, ifunct, ioffset;
    static int rsrc1, rsrc2, rfunct, rdes;
    static int inst, decodedInst;
    static int branch;
    static int address = 0x9A040;
    static int i;


    public static void main(String[] args) {
        fillMain_Mem();
        fillRegs();
        disassembler();
    }

    private static void fillMain_Mem() {
        int mmFillWholeList = 0x00;
        for (int repeatedFill = 0; repeatedFill <= 0x03; repeatedFill++) {
            for (int i = 0x00; i <= 0xff; i++) {
                Main_Mem[mmFillWholeList] = i;
                mmFillWholeList++;
                System.out.printf("%x", Main_Mem[i]);
                System.out.println();
            }
        }
    }

    private static void fillRegs() {
        int RegsStartsAt100 = 0x101;
        System.out.println("Regs");
        for (int i = 1; i < 32; i++) {
            Regs[i] = RegsStartsAt100;
            RegsStartsAt100++;
            System.out.printf("%x", Regs[i]);
            System.out.println();
        }
    }

    public static void disassembler() {
        for (int i = 0; i < arr.length; i++) {
            hexnum = arr[i];

            int opcode = (hexnum >>> 26);
            if (opcode == 0) {
                opcodeZeroAssingingSigns();
                opcodeZero();
            } if (opcode == 0x20) {
                    sign = "lw";
                    lwSw();
                }

                 if (opcode == 0x28) {
                    sign = "sw";
                    lwSw();
                }
            }
        }
public static void IF_stage(){
        disassembler();
        inst = arr[i];
        address=address+4;




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
        System.out.printf("0x%02X", address);
        System.out.println(": " + sign + " $" + rdes + ", " + "$" + rsrc1 + ", " + "$" + rsrc2);
        address = address + 0x04;
    }


    public static void lwSw() {
        isrc1 = (hexnum & 0x3e00000) >>> 21;
        isrc2 = (hexnum & 0x1f0000) >>> 16;
        ioffset = (byte) (hexnum & 0xffff);
        System.out.printf("0x%02X", address);
        System.out.println(": " + sign + " $" + isrc2 + ", " + ioffset + "(" + "$" + isrc1 + ")");
        address = address + 0x04;
    }

}