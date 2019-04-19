public class Pipeline {

    static int[] arr = new int[]{0xa1020000, 0x810AFFFC, 0x00831820, 0x01263820, 0x01224820, 0x81180000,
            0x81510010, 0x00624022, 0x00000000, 0x00000000, 0x00000000, 0x00000000};

    static int[] Main_Mem = new int[0x400];
    static int[] Regs = new int [32];

    public static void main(String[] args) {
        fillMain_Mem();
        fillRegs();
        disassembler();


    }



    private static void fillMain_Mem() {
        int mmFillWholeList = 0x00;
        //This part fills in 0-ff 7 times in MM*************************************
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
        int RegsStartsAt100=0x101;
        System.out.println("Regs");
        for (int i=1;i<32;i++){
            Regs[i]=RegsStartsAt100;
            RegsStartsAt100++;
            System.out.printf("%x", Regs[i]);
            System.out.println();
        }
    }

    public static void disassembler(){int hexnum;
        String sign=null;
        int isrc1, isrc2,  ioffset;//ifunct,
        int rsrc1, rsrc2, rfunct, rdes;
        int address= 0x9A040;

        for (int i=0; i<9; i++) {
            hexnum= arr[i];
            int opcode =  (hexnum >>>26);
            if (opcode == 0) {
                rsrc1 = (hexnum & 0x03e00000) >> 21;
                rsrc2 =  (hexnum & 0x1f0000)>>16;
                rdes =  (hexnum & 0xf800) >>> 11;
                rfunct = (hexnum & 0x3f);

                if (rfunct ==0x20) {
                    sign ="ADD";}

                if (rfunct ==0x22) {
                    sign ="SUB";}


                System.out.printf("0x%02X", address);
                System.out.println(": "+sign+ " $"+ rdes +", " + "$"+ rsrc1 +", "+"$"+ rsrc2);
                address= address+0x04;}

            else {
                isrc1 =  (hexnum & 0x3e00000) >>> 21;
                isrc2 =  (hexnum & 0x1f0000)>>>16;
                ioffset = (byte) (hexnum & 0xffff);
                //int branch =((ioffset<<2)+address+0x04);

                if (opcode == 0x23) {
                    sign ="LW";}

                if (opcode == 0x2b) {
                    sign ="SW";}

                switch(sign) {


                    case "LW":
                        System.out.printf("0x%02X", address);
                        System.out.println(": "+sign+ " $"+ isrc2  +", " + ioffset + "(" +"$"+ isrc1 +")");
                        address=address+0x04;
                        break;

                    case "SW":
                        System.out.printf("0x%02X", address);
                        System.out.println(": "+sign+ " $"+ isrc2  +", " + ioffset + "(" +"$"+ isrc1 +")");
                        address=address+0x04;
                        break;


                }

            }

        }

    }

    }



