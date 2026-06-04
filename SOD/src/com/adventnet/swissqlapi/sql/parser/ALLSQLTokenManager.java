package com.adventnet.swissqlapi.sql.parser;

import java.io.IOException;
import java.io.PrintStream;

public class ALLSQLTokenManager implements ALLSQLConstants {
   public PrintStream debugStream;
   static final long[] jjbitVec0 = new long[]{-2L, -1L, -1L, -1L};
   static final long[] jjbitVec2 = new long[]{0L, 0L, -1L, -1L};
   static final int[] jjnextStates = new int[]{287, 288, 289, 282, 283, 284, 272, 164, 202, 204, 206, 154, 155, 143, 261, 262, 256, 257, 246, 211, 258, 263, 268, 270, 281, 286, 242, 244, 255, 260, 135, 136, 137, 130, 131, 132, 75, 294, 296, 297, 298, 140, 154, 156, 158, 160, 161, 208, 241, 265, 266, 267, 291, 292, 85, 92, 101, 107, 116, 124, 127, 128, 129, 134, 35, 36, 25, 26, 28, 30, 32, 34, 25, 28, 29, 77, 79, 80, 81, 82, 88, 89, 91, 97, 98, 100, 103, 104, 106, 112, 113, 115, 119, 120, 122, 125, 126, 130, 131, 132, 75, 131, 132, 75, 135, 136, 137, 144, 147, 149, 151, 153, 163, 164, 202, 204, 206, 166, 176, 200, 201, 168, 169, 170, 172, 174, 177, 179, 190, 195, 178, 169, 170, 172, 174, 180, 181, 169, 170, 172, 174, 183, 185, 184, 169, 170, 172, 174, 186, 187, 188, 169, 170, 172, 174, 186, 169, 170, 172, 174, 187, 188, 169, 170, 172, 174, 189, 169, 170, 172, 174, 191, 192, 193, 181, 169, 170, 172, 174, 191, 181, 169, 170, 172, 174, 192, 193, 181, 169, 170, 172, 174, 194, 181, 169, 170, 172, 174, 196, 197, 198, 169, 170, 172, 174, 196, 169, 170, 172, 174, 197, 198, 169, 170, 172, 174, 199, 169, 170, 172, 174, 196, 197, 198, 191, 192, 193, 181, 169, 170, 172, 174, 206, 207, 165, 212, 215, 239, 240, 216, 218, 229, 234, 222, 224, 225, 226, 227, 230, 231, 232, 220, 231, 232, 220, 235, 236, 237, 235, 236, 237, 230, 231, 232, 220, 245, 246, 211, 248, 250, 251, 252, 211, 253, 254, 211, 212, 215, 239, 240, 256, 257, 246, 211, 258, 256, 246, 211, 257, 246, 258, 259, 246, 211, 212, 215, 239, 240, 259, 246, 211, 261, 262, 211, 263, 264, 211, 212, 215, 239, 240, 269, 164, 202, 204, 206, 271, 272, 164, 202, 204, 206, 274, 276, 275, 164, 202, 204, 206, 277, 278, 279, 164, 202, 204, 206, 277, 164, 202, 204, 206, 278, 279, 164, 202, 204, 206, 280, 164, 202, 204, 206, 282, 283, 284, 272, 164, 202, 204, 206, 282, 272, 164, 202, 204, 206, 283, 284, 272, 164, 202, 204, 206, 285, 272, 164, 202, 204, 206, 287, 288, 289, 164, 202, 204, 206, 287, 164, 202, 204, 206, 288, 289, 164, 202, 204, 206, 290, 164, 202, 204, 206, 294, 296, 297, 298, 22, 35, 37, 39, 34, 42, 43, 45, 47, 19, 20, 76, 77, 79, 94, 95, 109, 110, 141, 142, 145, 146, 182, 183, 185, 221, 222, 224, 247, 248, 250, 273, 274, 276};
   public static final String[] jjstrLiteralImages = new String[]{"", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, ",", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "+", "::", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "&", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, ".", "(", ")", "@", "*", "-", "/", "|", "%", "**", "^", "=", ":=", "~", "{", "}", "[", "]", "<", ">", "!", "^=", "~*", "!~", "!~*"};
   public static final String[] lexStateNames = new String[]{"DEFAULT"};
   static final long[] jjtoToken = new long[]{-511L, -1L, -1L, -1L, -1L, -1L, -1L, -1L, -4222133240594433L, 8191L};
   static final long[] jjtoSkip = new long[]{510L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};
   static final long[] jjtoSpecial = new long[]{480L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};
   protected JavaCharStream input_stream;
   private final int[] jjrounds;
   private final int[] jjstateSet;
   protected char curChar;
   int curLexState;
   int defaultLexState;
   int jjnewStateCnt;
   int jjround;
   int jjmatchedPos;
   int jjmatchedKind;

   public void setDebugStream(PrintStream ds) {
      this.debugStream = ds;
   }

   private final int jjStopStringLiteralDfa_0(int pos, long active0, long active1, long active2, long active3, long active4, long active5, long active6, long active7, long active8, long active9) {
      switch(pos) {
      case 0:
         if ((active0 & 29273466297384960L) == 0L && (active1 & 140739652616200L) == 0L && (active2 & 36028814198985728L) == 0L && (active3 & -9214364773175525376L) == 0L && (active4 & 72057594037927936L) == 0L && (active5 & 5782693389799522816L) == 0L && (active6 & -6329667984595087360L) == 0L && (active7 & 2305885924530585601L) == 0L && (active8 & 8392704L) == 0L) {
            if ((active0 & 60129542144L) == 0L && (active1 & 70403103916176L) == 0L && (active2 & 4398046511104L) == 0L && (active3 & 67108872L) == 0L && (active4 & 64L) == 0L && (active5 & 324399910728237184L) == 0L && (active6 & 412316860416L) == 0L && (active7 & 8847362L) == 0L) {
               if ((active8 & 36028797018963968L) != 0L) {
                  return 22;
               }

               if ((active8 & 4503599627370496L) != 0L) {
                  return 299;
               }

               if ((active8 & 144115188075855872L) != 0L) {
                  return 0;
               }

               if ((active8 & 288230376151711744L) != 0L) {
                  return 85;
               }

               if ((active0 & -1758655783339295232L) == 0L && (active1 & -211211878666395L) == 0L && (active2 & -36596162198918145L) == 0L && (active3 & 9214241627806105591L) == 0L && (active4 & -2419558899804799045L) == 0L && (active5 & -6107093300527794817L) == 0L && (active6 & 6329649980058628030L) == 0L && (active7 & -3461059246139834372L) == 0L && (active8 & 1059057662L) == 0L) {
                  if ((active1 & 69122134018L) == 0L && (active2 & 562949953421312L) == 0L && (active3 & 123145302310912L) == 0L && (active4 & 2347501305766871044L) == 0L && (active5 & 34816L) == 0L && (active6 & 17592219598913L) == 0L && (active7 & 1155173321600401408L) == 0L && (active8 & 6291457L) == 0L) {
                     if ((active8 & 1073741824L) != 0L) {
                        return 13;
                     }

                     if ((active9 & 16L) != 0L) {
                        return 301;
                     }

                     if ((active0 & 1152921504606846976L) == 0L && (active9 & 1L) == 0L) {
                        return -1;
                     }

                     return 302;
                  }

                  this.jjmatchedKind = 544;
                  return 5;
               }

               this.jjmatchedKind = 544;
               return 300;
            }

            this.jjmatchedKind = 544;
            return 9;
         }

         this.jjmatchedKind = 544;
         return 56;
      case 1:
         if ((active1 & 402653184L) == 0L && (active3 & 105553116266496L) == 0L && (active4 & 4L) == 0L) {
            if ((active0 & 261217162163699712L) == 0L && (active1 & 2306054133699837952L) == 0L && (active2 & 45035996810597376L) == 0L && (active3 & 10713705993994240L) == 0L && (active4 & 288230376151711744L) == 0L && (active5 & -3440679574629766656L) == 0L && (active6 & -6184990398466994136L) == 0L && (active7 & 63617L) == 0L && (active8 & 82112L) == 0L) {
               if ((active0 & -1990599419076067840L) == 0L && (active1 & -2306054134102491137L) == 0L && (active2 & -45035996810597377L) == 0L && (active3 & -10819259110260737L) == 0L && (active4 & -288230376151711813L) == 0L && (active5 & 3440679574629766655L) == 0L && (active6 & 6184990398466994135L) == 0L && (active7 & -63618L) == 0L && (active8 & 1073659711L) == 0L) {
                  if ((active4 & 64L) != 0L) {
                     if (this.jjmatchedPos != 1) {
                        this.jjmatchedKind = 544;
                        this.jjmatchedPos = 1;
                     }

                     return 8;
                  }

                  return -1;
               }

               if (this.jjmatchedPos != 1) {
                  this.jjmatchedKind = 544;
                  this.jjmatchedPos = 1;
               }

               return 300;
            }

            return 300;
         }

         if (this.jjmatchedPos != 1) {
            this.jjmatchedKind = 544;
            this.jjmatchedPos = 1;
         }

         return 4;
      case 2:
         if ((active0 & 7376888877448898560L) == 0L && (active1 & -4359748323159081473L) == 0L && (active2 & -2738399681284409345L) == 0L && (active3 & -153212547820953601L) == 0L && (active4 & 3891091936102055887L) == 0L && (active5 & -2414140558600710145L) == 0L && (active6 & -70368744181769L) == 0L && (active7 & -720575940387207299L) == 0L && (active8 & 536821567L) == 0L) {
            if ((active0 & -9214360411636197888L) == 0L && (active1 & 2053834945201209856L) == 0L && (active2 & 2738399680747538432L) == 0L && (active3 & 153124586890731520L) == 0L && (active4 & -3891091936102055888L) == 0L && (active5 & 2414070155496794112L) == 0L && (active6 & 70368744177672L) == 0L && (active7 & 720575940387207170L) == 0L && (active8 & 536920256L) == 0L) {
               if ((active3 & 70368744177664L) != 0L) {
                  if (this.jjmatchedPos != 2) {
                     this.jjmatchedKind = 544;
                     this.jjmatchedPos = 2;
                  }

                  return 303;
               }

               return -1;
            }

            return 300;
         }

         if (this.jjmatchedPos != 2) {
            this.jjmatchedKind = 544;
            this.jjmatchedPos = 2;
         }

         return 300;
      case 3:
         if ((active0 & 22654372274110464L) == 0L && (active1 & 31560528804060460L) == 0L && (active2 & -9218305478214287224L) == 0L && (active3 & -9223090336186236672L) == 0L && (active4 & 2806868467759022153L) == 0L && (active5 & 281751264919557L) == 0L && (active6 & 1147895558439520L) == 0L && (active7 & 5792342223127314992L) == 0L && (active8 & 442963972L) == 0L) {
            if ((active0 & 7354238911811233792L) == 0L && (active1 & -2337632237510106925L) == 0L && (active2 & 6768347279314124663L) == 0L && (active3 & 9223072744000192255L) == 0L && (active4 & -7418572078372454474L) == 0L && (active5 & -352172102354950L) == 0L && (active6 & -1147895558443617L) == 0L && (active7 & -6512918163507704497L) == 0L && (active8 & 93874171L) == 0L) {
               if ((active7 & 57344L) != 0L) {
                  return 17;
               }

               return -1;
            }

            if (this.jjmatchedPos != 3) {
               this.jjmatchedKind = 544;
               this.jjmatchedPos = 3;
            }

            return 300;
         }

         return 300;
      case 4:
         if ((active0 & 145821698842173440L) == 0L && (active1 & 2155945986L) == 0L && (active2 & 2017612648161476656L) == 0L && (active3 & -8606365418619137008L) == 0L && (active4 & 72057602629964800L) == 0L && (active5 & -8502653144753224544L) == 0L && (active6 & 4611721753368985613L) == 0L && (active7 & 2595235569299030016L) == 0L && (active8 & 1901056L) == 0L) {
            if ((active0 & 7208434805155104768L) == 0L && (active1 & -2310610632506588715L) == 0L && (active2 & 4755801189524701007L) == 0L && (active3 & 8606347669662067439L) == 0L && (active4 & -7454600883983193162L) == 0L && (active5 & 8502301247528809306L) == 0L && (active6 & -4612852056741384302L) == 0L && (active7 & -4487319227863659169L) == 0L && (active8 & 534801915L) == 0L) {
               if ((active0 & 105553116266496L) != 0L) {
                  return 17;
               }

               return -1;
            }

            if (this.jjmatchedPos != 4) {
               this.jjmatchedKind = 544;
               this.jjmatchedPos = 4;
            }

            return 300;
         }

         return 300;
      case 5:
         if ((active0 & 2597591835618904064L) == 0L && (active1 & -7066426184234171947L) == 0L && (active2 & 4755518907127594793L) == 0L && (active3 & 3478857656590181615L) == 0L && (active4 & -7959013929401816154L) == 0L && (active5 & 8502282487110599514L) == 0L && (active6 & -4651132653574574445L) == 0L && (active7 & -4523365619753546467L) == 0L && (active8 & 432041435L) == 0L) {
            if ((active0 & 4611968869443043328L) == 0L && (active1 & 4755815551727648768L) == 0L && (active2 & 282282464215110L) == 0L && (active3 & 5127490013340322304L) == 0L && (active4 & 576470648046485520L) == 0L && (active5 & 18760418209792L) == 0L && (active6 & 38281146589004032L) == 0L && (active7 & 36046391889887298L) == 0L && (active8 & 103284768L) == 0L) {
               if ((active7 & 393216L) != 0L) {
                  return 17;
               }

               return -1;
            }

            return 300;
         }

         if (this.jjmatchedPos != 5) {
            this.jjmatchedKind = 544;
            this.jjmatchedPos = 5;
         }

         return 300;
      case 6:
         if ((active0 & 2309220717541263360L) == 0L && (active1 & 3032249008192L) == 0L && (active2 & 72129886927850024L) == 0L && (active3 & 1152956693410644096L) == 0L && (active4 & -8067633583415228126L) == 0L && (active5 & 3510032542000480344L) == 0L && (active6 & -9222799189148131310L) == 0L && (active7 & 70798263977985L) == 0L && (active8 & 8396808L) == 0L) {
            if ((active4 & 70368744177664L) != 0L) {
               if (this.jjmatchedPos < 5) {
                  this.jjmatchedKind = 544;
                  this.jjmatchedPos = 5;
               }

               return -1;
            }

            if ((active0 & 288371118077640704L) == 0L && (active1 & -2310628009979936363L) == 0L && (active2 & 4683389569989113089L) == 0L && (active3 & 2400210357031281775L) == 0L && (active4 & 108558081362256516L) == 0L && (active5 & 4992249945110119170L) == 0L && (active6 & 4571666535573557121L) == 0L && (active7 & -4523436418017524452L) == 0L && (active8 & 490753491L) == 0L) {
               return -1;
            }

            if (this.jjmatchedPos != 6) {
               this.jjmatchedKind = 544;
               this.jjmatchedPos = 6;
            }

            return 300;
         }

         return 300;
      case 7:
         if ((active0 & 288230380580896768L) == 0L && (active1 & 46443371157258516L) == 0L && (active2 & 4616752568076861696L) == 0L && (active3 & 914862477680640L) == 0L && (active4 & 72484591113880196L) == 0L && (active5 & 2261145662521610L) == 0L && (active6 & 1535727885323730944L) == 0L && (active7 & 4623092395003609088L) == 0L && (active8 & 419450051L) == 0L) {
            if ((active0 & 140737488355328L) != 0L) {
               if (this.jjmatchedPos < 6) {
                  this.jjmatchedKind = 544;
                  this.jjmatchedPos = 6;
               }

               return -1;
            }

            if ((active0 & 8598323200L) == 0L && (active1 & -2357071381137194879L) == 0L && (active2 & 66637001912251393L) == 0L && (active3 & 2399295494553601135L) == 0L && (active4 & 36073490248376320L) == 0L && (active5 & 4998995998702338560L) == 0L && (active6 & 3036501600203247489L) == 0L && (active7 & -9146528813021133540L) == 0L && (active8 & 71303440L) == 0L) {
               if ((active4 & 70368744177664L) != 0L) {
                  if (this.jjmatchedPos < 5) {
                     this.jjmatchedKind = 544;
                     this.jjmatchedPos = 5;
                  }

                  return -1;
               }

               if ((active7 & 274877906944L) != 0L) {
                  return 17;
               }

               return -1;
            }

            if (this.jjmatchedPos != 7) {
               this.jjmatchedKind = 544;
               this.jjmatchedPos = 7;
            }

            return 300;
         }

         return 300;
      case 8:
         if ((active0 & 8598323200L) == 0L && (active1 & -6985364423208337279L) == 0L && (active2 & 20327771167285249L) == 0L && (active3 & 2309222951981941824L) == 0L && (active4 & 13769464872960L) == 0L && (active5 & 378302781015982592L) == 0L && (active6 & 2891823427814231809L) == 0L && (active7 & -9218586962183585508L) == 0L && (active8 & 335544592L) == 0L) {
            if ((active1 & 4629700416954695680L) == 0L && (active2 & 46309230744966144L) == 0L && (active3 & 90072542571659311L) == 0L && (active4 & 36064118830014464L) == 0L && (active5 & 4620693217686355968L) == 0L && (active6 & 144678309827969152L) == 0L && (active7 & 72058149162451968L) == 0L && (active8 & 4194304L) == 0L) {
               if ((active0 & 140737488355328L) != 0L) {
                  if (this.jjmatchedPos < 6) {
                     this.jjmatchedKind = 544;
                     this.jjmatchedPos = 6;
                  }

                  return -1;
               }

               if ((active4 & 70368744177664L) != 0L) {
                  if (this.jjmatchedPos < 5) {
                     this.jjmatchedKind = 544;
                     this.jjmatchedPos = 5;
                  }

                  return -1;
               }

               if ((active7 & 4398046511104L) == 0L && (active8 & 134217728L) == 0L) {
                  return -1;
               }

               return 17;
            }

            return 300;
         }

         if (this.jjmatchedPos != 8) {
            this.jjmatchedKind = 544;
            this.jjmatchedPos = 8;
         }

         return 300;
      case 9:
         if ((active0 & 8388608L) == 0L && (active1 & 73551315072L) == 0L && (active2 & 1169880564891648L) == 0L && (active3 & 3379934178313280L) == 0L && (active4 & 4947803373568L) == 0L && (active5 & 360287970189640192L) == 0L && (active6 & 2891592461652919297L) == 0L && (active7 & -9218868437160296184L) == 0L && (active8 & 16L) == 0L) {
            if ((active0 & 140737488355328L) != 0L) {
               if (this.jjmatchedPos < 6) {
                  this.jjmatchedKind = 544;
                  this.jjmatchedPos = 6;
               }

               return -1;
            }

            if ((active4 & 70368744177664L) != 0L) {
               if (this.jjmatchedPos < 5) {
                  this.jjmatchedKind = 544;
                  this.jjmatchedPos = 5;
               }

               return -1;
            }

            if ((active0 & 8589934592L) == 0L && (active1 & -6985364496759652351L) == 0L && (active2 & 20283790509236225L) == 0L && (active3 & 2305843567559442432L) == 0L && (active4 & 8821661499392L) == 0L && (active5 & 18014810826342400L) == 0L && (active6 & 230966161312512L) == 0L && (active7 & 281474976710676L) == 0L && (active8 & 339738880L) == 0L) {
               return -1;
            }

            if (this.jjmatchedPos != 9) {
               this.jjmatchedKind = 544;
               this.jjmatchedPos = 9;
            }

            return 300;
         }

         return 300;
      case 10:
         if ((active1 & -9151314442548412416L) == 0L && (active2 & 8192L) == 0L && (active6 & 2267742732288L) == 0L && (active7 & 256L) == 0L && (active8 & 71303168L) == 0L) {
            if ((active4 & 70368744177664L) != 0L) {
               if (this.jjmatchedPos < 5) {
                  this.jjmatchedKind = 544;
                  this.jjmatchedPos = 5;
               }

               return -1;
            }

            if ((active0 & 8589934592L) == 0L && (active1 & 2165949945788760065L) == 0L && (active2 & 20283790509228033L) == 0L && (active3 & 2305843567559442432L) == 0L && (active4 & 8821661499392L) == 0L && (active5 & 18014810826342400L) == 0L && (active6 & 228698418581248L) == 0L && (active7 & 281474976710676L) == 0L && (active8 & 268435712L) == 0L) {
               return -1;
            }

            this.jjmatchedKind = 544;
            this.jjmatchedPos = 10;
            return 300;
         }

         return 300;
      case 11:
         if ((active0 & 8589934592L) == 0L && (active1 & 2162572246067970049L) == 0L && (active2 & 17592186060800L) == 0L && (active4 & 8821393063936L) == 0L && (active5 & 274877906944L) == 0L && (active6 & 211106232536832L) == 0L && (active7 & 281474976710676L) == 0L && (active8 & 268435456L) == 0L) {
            if ((active1 & 3377699720790016L) == 0L && (active2 & 20266198323167233L) == 0L && (active3 & 2305843567559442432L) == 0L && (active4 & 268435456L) == 0L && (active5 & 18014535948435456L) == 0L && (active6 & 17592186044416L) == 0L && (active8 & 256L) == 0L) {
               if ((active4 & 70368744177664L) != 0L) {
                  if (this.jjmatchedPos < 5) {
                     this.jjmatchedKind = 544;
                     this.jjmatchedPos = 5;
                  }

                  return -1;
               }

               return -1;
            }

            return 300;
         }

         if (this.jjmatchedPos != 11) {
            this.jjmatchedKind = 544;
            this.jjmatchedPos = 11;
         }

         return 300;
      case 12:
         if ((active1 & 288230376151711744L) == 0L && (active2 & 2269391999746048L) == 0L && (active4 & 2214592512L) == 0L && (active6 & 512L) == 0L && (active7 & 281474976710676L) == 0L) {
            if ((active4 & 70368744177664L) != 0L) {
               if (this.jjmatchedPos < 5) {
                  this.jjmatchedKind = 544;
                  this.jjmatchedPos = 5;
               }

               return -1;
            }

            if ((active0 & 8589934592L) == 0L && (active1 & 1874341869916258305L) == 0L && (active4 & 8819178471424L) == 0L && (active5 & 274877906944L) == 0L && (active6 & 211106232536320L) == 0L && (active8 & 268435456L) == 0L) {
               return -1;
            }

            this.jjmatchedKind = 544;
            this.jjmatchedPos = 12;
            return 300;
         }

         return 300;
      case 13:
         if ((active1 & 144678138029277184L) == 0L && (active4 & 4831838208L) == 0L && (active5 & 274877906944L) == 0L && (active6 & 2304L) == 0L && (active8 & 268435456L) == 0L) {
            if ((active4 & 70368744177664L) != 0L) {
               if (this.jjmatchedPos < 5) {
                  this.jjmatchedKind = 544;
                  this.jjmatchedPos = 5;
               }

               return -1;
            }

            if ((active0 & 8589934592L) == 0L && (active1 & 1729663731886981121L) == 0L && (active4 & 8814346633216L) == 0L && (active6 & 211106232534016L) == 0L) {
               return -1;
            }

            this.jjmatchedKind = 544;
            this.jjmatchedPos = 13;
            return 300;
         }

         return 300;
      case 14:
         if ((active1 & 576460752303423488L) == 0L && (active4 & 17179869184L) == 0L && (active6 & 70368744177664L) == 0L) {
            if ((active0 & 8589934592L) == 0L && (active1 & 1153202979583557633L) == 0L && (active4 & 8797166764032L) == 0L && (active6 & 140737488356352L) == 0L) {
               return -1;
            }

            this.jjmatchedKind = 544;
            this.jjmatchedPos = 14;
            return 300;
         }

         return 300;
      case 15:
         if ((active1 & 1152921504606846976L) == 0L && (active4 & 8797166764032L) == 0L) {
            if ((active0 & 8589934592L) == 0L && (active1 & 281474976710657L) == 0L && (active6 & 140737488356352L) == 0L) {
               return -1;
            }

            this.jjmatchedKind = 544;
            this.jjmatchedPos = 15;
            return 300;
         }

         return 300;
      case 16:
         if ((active1 & 281474976710656L) == 0L && (active6 & 140737488355328L) == 0L) {
            if ((active0 & 8589934592L) == 0L && (active1 & 1L) == 0L && (active6 & 1024L) == 0L) {
               return -1;
            }

            this.jjmatchedKind = 544;
            this.jjmatchedPos = 16;
            return 300;
         }

         return 300;
      case 17:
         if ((active0 & 8589934592L) != 0L) {
            return 300;
         } else {
            if ((active1 & 1L) == 0L && (active6 & 1024L) == 0L) {
               return -1;
            }

            this.jjmatchedKind = 544;
            this.jjmatchedPos = 17;
            return 300;
         }
      case 18:
         if ((active1 & 1L) != 0L) {
            return 300;
         } else {
            if ((active6 & 1024L) != 0L) {
               this.jjmatchedKind = 544;
               this.jjmatchedPos = 18;
               return 300;
            }

            return -1;
         }
      case 19:
         if ((active6 & 1024L) != 0L) {
            this.jjmatchedKind = 544;
            this.jjmatchedPos = 19;
            return 300;
         }

         return -1;
      case 20:
         if ((active6 & 1024L) != 0L) {
            this.jjmatchedKind = 544;
            this.jjmatchedPos = 20;
            return 300;
         }

         return -1;
      default:
         return -1;
      }
   }

   private final int jjStartNfa_0(int pos, long active0, long active1, long active2, long active3, long active4, long active5, long active6, long active7, long active8, long active9) {
      return this.jjMoveNfa_0(this.jjStopStringLiteralDfa_0(pos, active0, active1, active2, active3, active4, active5, active6, active7, active8, active9), pos + 1);
   }

   private int jjStopAtPos(int pos, int kind) {
      this.jjmatchedKind = kind;
      this.jjmatchedPos = pos;
      return pos + 1;
   }

   private int jjMoveStringLiteralDfa0_0() {
      switch(this.curChar) {
      case '!':
         this.jjmatchedKind = 584;
         return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 6144L);
      case '"':
      case '#':
      case '$':
      case '\'':
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
      case ';':
      case '?':
      case '\\':
      case '_':
      case '`':
      default:
         return this.jjMoveNfa_0(2, 0);
      case '%':
         return this.jjStopAtPos(0, 572);
      case '&':
         return this.jjStartNfaWithStates_0(0, 542, 13);
      case '(':
         return this.jjStopAtPos(0, 565);
      case ')':
         return this.jjStopAtPos(0, 566);
      case '*':
         this.jjmatchedKind = 568;
         return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 2305843009213693952L, 0L);
      case '+':
         return this.jjStopAtPos(0, 59);
      case ',':
         return this.jjStopAtPos(0, 21);
      case '-':
         return this.jjStartNfaWithStates_0(0, 569, 0);
      case '.':
         return this.jjStartNfaWithStates_0(0, 564, 299);
      case '/':
         return this.jjStartNfaWithStates_0(0, 570, 85);
      case ':':
         return this.jjMoveStringLiteralDfa1_0(1152921504606846976L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 1L);
      case '<':
         return this.jjStopAtPos(0, 582);
      case '=':
         return this.jjStopAtPos(0, 575);
      case '>':
         return this.jjStopAtPos(0, 583);
      case '@':
         return this.jjStartNfaWithStates_0(0, 567, 22);
      case 'A':
      case 'a':
         return this.jjMoveStringLiteralDfa1_0(65024L, 4359484439865065472L, 536903680L, 13328L, 4294967296L, 17892833755137L, 35184372088840L, 0L, 0L, 0L);
      case 'B':
      case 'b':
         return this.jjMoveStringLiteralDfa1_0(137439019008L, 137438953472L, 512L, 4503599627386880L, 4120L, 0L, 2199291691040L, 468937313397768192L, 65568L, 0L);
      case 'C':
      case 'c':
         return this.jjMoveStringLiteralDfa1_0(4503599641919488L, 4611695089398317056L, 554050781192L, 1152921504607338496L, 2256206458535936L, 585503204683284496L, 4611686018427559936L, 9414568313120L, 9216L, 0L);
      case 'D':
      case 'd':
         return this.jjMoveStringLiteralDfa1_0(251658240L, 27074374322356228L, 140517442L, 281474984575008L, 18298089728638976L, 0L, 281474976711168L, 158330215465984L, 939556864L, 0L);
      case 'E':
      case 'e':
         return this.jjMoveStringLiteralDfa1_0(8321499136L, 2251808403619840L, 1099511628036L, 2251799872405504L, 0L, 266250L, 18014398509481984L, 8L, 4L, 0L);
      case 'F':
      case 'f':
         return this.jjMoveStringLiteralDfa1_0(60129542144L, 70403103916176L, 4398046511104L, 67108872L, 64L, 324399910728237184L, 412316860416L, 8847362L, 0L, 0L);
      case 'G':
      case 'g':
         return this.jjMoveStringLiteralDfa1_0(68719476736L, 2164260864L, 0L, 0L, 72057594037927936L, 1099511627776L, 549755817984L, 0L, 8392704L, 0L);
      case 'H':
      case 'h':
         return this.jjMoveStringLiteralDfa1_0(274877906944L, 0L, 0L, 134217728L, 67108864L, 281474976710912L, 1073741824L, 0L, 0L, 0L);
      case 'I':
      case 'i':
         return this.jjMoveStringLiteralDfa1_0(8246337208320L, 145241105162567680L, 9007199254740992L, 1688850128699456L, 134225920L, -9221120236902678528L, 144678140178858240L, 1125899906906240L, 16576L, 0L);
      case 'J':
      case 'j':
         return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 0L, 268435456L, 0L, 0L, 0L, 0L);
      case 'K':
      case 'k':
         return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 0L, 536870912L, 4398046511104L, 0L, 0L, 0L);
      case 'L':
      case 'l':
         return this.jjMoveStringLiteralDfa1_0(131941395333120L, 4503599627374080L, 344672176128L, 0L, 576460752573956099L, 2305845209310707712L, 16777216L, 4638707616191611392L, 2048L, 0L);
      case 'M':
      case 'm':
         return this.jjMoveStringLiteralDfa1_0(1125899906842624L, 281474976972864L, 76561193665298480L, 3758096896L, 17695265259520L, 144115188084244480L, 1441151906532556800L, -9218868432798220288L, 1573376L, 0L);
      case 'N':
      case 'n':
         return this.jjMoveStringLiteralDfa1_0(29273397577908224L, 140737488355336L, 36028814198985728L, -9214364773175525376L, 0L, 5782692290287895040L, -6329668534350905344L, 2305885924530585601L, 0L, 0L);
      case 'O':
      case 'o':
         return this.jjMoveStringLiteralDfa1_0(252201579132747776L, 1075839232L, 281475010265088L, 481036337152L, 288793326121910272L, 173947232256L, 1099511644160L, 0L, 0L, 0L);
      case 'P':
      case 'p':
         return this.jjMoveStringLiteralDfa1_0(0L, 1103806595072L, 595609846829547520L, 54043745284259972L, 1152921916923707904L, 6197397289959460L, 68786585728L, 1073741824L, 0L, 0L);
      case 'Q':
      case 'q':
         return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 1024L, 0L, 0L, 0L, 0L, 0L);
      case 'R':
      case 'r':
         return this.jjMoveStringLiteralDfa1_0(2306687434143825920L, 562949954469888L, 1585304495246868480L, 216176080648669440L, 549755947168L, 72066390148383744L, 1205065290350592L, 576460752571859008L, 67108864L, 0L);
      case 'S':
      case 's':
         return this.jjMoveStringLiteralDfa1_0(-4323314904787320832L, 72064740863508513L, -6915259635372916607L, 3170547331808362497L, 144230640554868992L, 65536L, 108086391056960518L, 281483633754116L, 256L, 0L);
      case 'T':
      case 't':
         return this.jjMoveStringLiteralDfa1_0(0L, 69122134018L, 562949953421312L, 123145302310912L, 2347501305766871044L, 34816L, 17592219598913L, 1155173321600401408L, 6291457L, 0L);
      case 'U':
      case 'u':
         return this.jjMoveStringLiteralDfa1_0(0L, -9223372036854718464L, 0L, 4611826755915743234L, 140737488355328L, 4294967360L, 34359738384L, 0L, 26L, 0L);
      case 'V':
      case 'v':
         return this.jjMoveStringLiteralDfa1_0(0L, 196608L, 4611686158014349312L, 0L, 0L, 0L, 4503599627370496L, 72128375098966032L, 0L, 0L);
      case 'W':
      case 'w':
         return this.jjMoveStringLiteralDfa1_0(0L, 80216064L, 8388608L, 0L, 360448L, 0L, 0L, 0L, 33554432L, 0L);
      case 'X':
      case 'x':
         return this.jjMoveStringLiteralDfa1_0(0L, 0L, 211106232532992L, 0L, -4611686018427387904L, 0L, 0L, 0L, 0L, 0L);
      case 'Y':
      case 'y':
         return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 393216L, 0L);
      case 'Z':
      case 'z':
         return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 9007199254740992L, 0L, 0L, 0L, 16777216L, 0L);
      case '[':
         return this.jjStartNfaWithStates_0(0, 580, 301);
      case ']':
         return this.jjStopAtPos(0, 581);
      case '^':
         this.jjmatchedKind = 574;
         return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 512L);
      case '{':
         return this.jjStopAtPos(0, 578);
      case '|':
         return this.jjStopAtPos(0, 571);
      case '}':
         return this.jjStopAtPos(0, 579);
      case '~':
         this.jjmatchedKind = 577;
         return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 1024L);
      }
   }

   private int jjMoveStringLiteralDfa1_0(long active0, long active1, long active2, long active3, long active4, long active5, long active6, long active7, long active8, long active9) {
      try {
         this.curChar = this.input_stream.readChar();
      } catch (IOException var22) {
         this.jjStopStringLiteralDfa_0(0, active0, active1, active2, active3, active4, active5, active6, active7, active8, active9);
         return 1;
      }

      switch(this.curChar) {
      case '*':
         if ((active8 & 2305843009213693952L) != 0L) {
            return this.jjStopAtPos(1, 573);
         }

         if ((active9 & 1024L) != 0L) {
            return this.jjStopAtPos(1, 586);
         }
      case '+':
      case ',':
      case '-':
      case '.':
      case '/':
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
      case ';':
      case '<':
      case '>':
      case '?':
      case '@':
      case 'J':
      case 'Z':
      case '[':
      case '\\':
      case ']':
      case '^':
      case '_':
      case '`':
      case 'j':
      case 'z':
      case '{':
      case '|':
      case '}':
      default:
         break;
      case ':':
         if ((active0 & 1152921504606846976L) != 0L) {
            return this.jjStopAtPos(1, 60);
         }
         break;
      case '=':
         if ((active9 & 1L) != 0L) {
            return this.jjStopAtPos(1, 576);
         }

         if ((active9 & 512L) != 0L) {
            return this.jjStopAtPos(1, 585);
         }
         break;
      case 'A':
      case 'a':
         return this.jjMoveStringLiteralDfa2_0(active0, 7881574225936384L, active1, 27355849299394638L, active2, 73183633682727456L, active3, 306526799528493569L, active4, 18014845186080833L, active5, 720615522797879300L, active6, 292733994112385153L, active7, 648729899250483200L, active8, 402653184L, active9, 0L);
      case 'B':
      case 'b':
         return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0L, active2, 281474976710656L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 17592186044416L, active8, 0L, active9, 0L);
      case 'C':
      case 'c':
         return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0L, active2, 2323866203816230912L, active3, 4398046511104L, active4, 0L, active5, 1706442046308352L, active6, 68719476736L, active7, 36283883716612L, active8, 0L, active9, 0L);
      case 'D':
      case 'd':
         return this.jjMoveStringLiteralDfa2_0(active0, 1024L, active1, 1125899906842624L, active2, 0L, active3, 1104L, active4, 0L, active5, 2251799813685248L, active6, 0L, active7, 0L, active8, 768L, active9, 0L);
      case 'E':
      case 'e':
         return this.jjMoveStringLiteralDfa2_0(active0, -2305561534119477248L, active1, 563568446537728L, active2, -3314048863477235629L, active3, 72058693550622720L, active4, 1298206590273980706L, active5, 72347866720927744L, active6, 73495759559852032L, active7, -8065946928052961280L, active8, 84312064L, active9, 0L);
      case 'F':
      case 'f':
         if ((active5 & 34359738368L) != 0L) {
            this.jjmatchedKind = 355;
            this.jjmatchedPos = 1;
         } else if ((active7 & 128L) != 0L) {
            return this.jjStartNfaWithStates_0(1, 455, 300);
         }

         return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 1073741824L, active2, 0L, active3, 4096L, active4, 0L, active5, 8192L, active6, 0L, active7, 288230376151711744L, active8, 0L, active9, 0L);
      case 'G':
      case 'g':
         return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 144115188075855872L, active2, 0L, active3, 0L, active4, 134217728L, active5, 0L, active6, 256L, active7, 0L, active8, 0L, active9, 0L);
      case 'H':
      case 'h':
         return this.jjMoveStringLiteralDfa2_0(active0, 786432L, active1, 12587008L, active2, 4303356040L, active3, 196608L, active4, 8589934592L, active5, 0L, active6, 33554432L, active7, 618475290624L, active8, 0L, active9, 0L);
      case 'I':
      case 'i':
         return this.jjMoveStringLiteralDfa2_0(active0, 288503055169617920L, active1, 34360394928L, active2, 4202496L, active3, 2097184L, active4, 2346445843391152144L, active5, 288230376160117920L, active6, 1152921513196781568L, active7, 182395804244246544L, active8, 576716801L, active9, 0L);
      case 'K':
      case 'k':
         return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0L, active2, 268435456L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L, active9, 0L);
      case 'L':
      case 'l':
         return this.jjMoveStringLiteralDfa2_0(active0, 562950221865472L, active1, 2053641430114500608L, active2, 1099511627776L, active3, 4503599627378688L, active4, 0L, active5, 9008298766368776L, active6, 8192L, active7, 571746046902272L, active8, 0L, active9, 0L);
      case 'M':
      case 'm':
         return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0L, active2, 211106232532992L, active3, 0L, active4, -4611686018427379712L, active5, 0L, active6, 0L, active7, 1407383540596736L, active8, 0L, active9, 0L);
      case 'N':
      case 'n':
         if ((active0 & 549755813888L) != 0L) {
            this.jjmatchedKind = 39;
            this.jjmatchedPos = 1;
         } else if ((active0 & 36028797018963968L) != 0L) {
            this.jjmatchedKind = 55;
            this.jjmatchedPos = 1;
         } else if ((active1 & 70368744177664L) != 0L) {
            return this.jjStartNfaWithStates_0(1, 110, 300);
         }

         return this.jjMoveStringLiteralDfa2_0(active0, 5499705628672L, active1, -9223372019674898432L, active2, 9007199254741252L, active3, 4613515606052831234L, active4, 288371113642164224L, active5, -9223372019536494528L, active6, 144678174538612752L, active7, 63496L, active8, 16606L, active9, 0L);
      case 'O':
      case 'o':
         if ((active3 & 17592186044416L) != 0L) {
            this.jjmatchedKind = 236;
            this.jjmatchedPos = 1;
         } else if ((active5 & 70368744177664L) != 0L) {
            this.jjmatchedKind = 366;
            this.jjmatchedPos = 1;
         } else if ((active6 & 4096L) != 0L) {
            return this.jjStartNfaWithStates_0(1, 396, 300);
         }

         return this.jjMoveStringLiteralDfa2_0(active0, 9007225037127680L, active1, 4688397020619082240L, active2, 328763598506580992L, active3, 1306046159143567624L, active4, 585490491823374472L, active5, 8124636733292874512L, active6, -6329598169901529088L, active7, 4638707616913032003L, active8, 1583104L, active9, 0L);
      case 'P':
      case 'p':
         return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 2113536L, active2, 33554432L, active3, 8796093022208L, active4, 16777216L, active5, 8590983168L, active6, 36029896530591744L, active7, 0L, active8, 32L, active9, 0L);
      case 'Q':
      case 'q':
         return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 1L, active2, 0L, active3, 0L, active4, 1610612736L, active5, 0L, active6, 0L, active7, 0L, active8, 0L, active9, 0L);
      case 'R':
      case 'r':
         if ((active0 & 72057594037927936L) != 0L) {
            this.jjmatchedKind = 56;
            this.jjmatchedPos = 1;
         }

         return this.jjMoveStringLiteralDfa2_0(active0, 144115291156119552L, active1, 6845104128L, active2, 4398046511104L, active3, 36134350139424900L, active4, 72057594037928452L, active5, 4504286855692288L, active6, 962072674816L, active7, 1073741824L, active8, 8388608L, active9, 0L);
      case 'S':
      case 's':
         if ((active0 & 16384L) != 0L) {
            this.jjmatchedKind = 14;
            this.jjmatchedPos = 1;
         } else if ((active0 & 2199023255552L) != 0L) {
            return this.jjStartNfaWithStates_0(1, 41, 300);
         }

         return this.jjMoveStringLiteralDfa2_0(active0, 5368741888L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 4294967296L, active6, 8L, active7, 0L, active8, 0L, active9, 0L);
      case 'T':
      case 't':
         if ((active2 & 536870912L) != 0L) {
            this.jjmatchedKind = 157;
            this.jjmatchedPos = 1;
         }

         return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 2305843009213726720L, active2, 0L, active3, 576460752303423488L, active4, 1101659111424L, active5, 0L, active6, 805372934L, active7, 2305843009213693952L, active8, 0L, active9, 0L);
      case 'U':
      case 'u':
         return this.jjMoveStringLiteralDfa2_0(active0, 18014398509481984L, active1, 1237487452160L, active2, 578730144303153152L, active3, -6917529027573972992L, active4, 3096224743822336L, active5, 277092499457L, active6, 37383395344384L, active7, 3671072L, active8, 5120L, active9, 0L);
      case 'V':
      case 'v':
         return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 256L, active2, 0L, active3, 206158430208L, active4, 4294967296L, active5, 0L, active6, 0L, active7, 6597069766656L, active8, 0L, active9, 0L);
      case 'W':
      case 'w':
         return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0L, active2, 0L, active3, 274877906944L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L, active9, 0L);
      case 'X':
      case 'x':
         return this.jjMoveStringLiteralDfa2_0(active0, 536870912L, active1, 2251808403619840L, active2, 0L, active3, 2251799864016896L, active4, 0L, active5, 266242L, active6, 18014398509481984L, active7, 0L, active8, 0L, active9, 0L);
      case 'Y':
      case 'y':
         if ((active0 & 137438953472L) != 0L) {
            this.jjmatchedKind = 37;
            this.jjmatchedPos = 1;
         }

         return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 6597069766656L, active2, 0L, active3, 0L, active4, 2199023255552L, active5, 0L, active6, 4611686018427388000L, active7, 0L, active8, 65536L, active9, 0L);
      case '~':
         if ((active9 & 2048L) != 0L) {
            this.jjmatchedKind = 587;
            this.jjmatchedPos = 1;
         }

         return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L, active9, 4096L);
      }

      return this.jjStartNfa_0(0, active0, active1, active2, active3, active4, active5, active6, active7, active8, active9);
   }

   private int jjMoveStringLiteralDfa2_0(long old0, long active0, long old1, long active1, long old2, long active2, long old3, long active3, long old4, long active4, long old5, long active5, long old6, long active6, long old7, long active7, long old8, long active8, long old9, long active9) {
      if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2) | (active3 &= old3) | (active4 &= old4) | (active5 &= old5) | (active6 &= old6) | (active7 &= old7) | (active8 &= old8) | (active9 &= old9)) == 0L) {
         return this.jjStartNfa_0(0, old0, old1, old2, old3, old4, old5, old6, old7, old8, old9);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var42) {
            this.jjStopStringLiteralDfa_0(1, active0, active1, active2, active3, active4, active5, active6, active7, active8, active9);
            return 2;
         }

         switch(this.curChar) {
         case ' ':
            return this.jjMoveStringLiteralDfa3_0(active0, 0L, active1, 2305843009213693952L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L, active9, 0L);
         case '*':
            if ((active9 & 4096L) != 0L) {
               return this.jjStopAtPos(2, 588);
            }
         case '!':
         case '"':
         case '#':
         case '$':
         case '%':
         case '&':
         case '\'':
         case '(':
         case ')':
         case '+':
         case ',':
         case '-':
         case '.':
         case '/':
         case '0':
         case '1':
         case '2':
         case '3':
         case '4':
         case '5':
         case '6':
         case '7':
         case '8':
         case '9':
         case ':':
         case ';':
         case '<':
         case '=':
         case '>':
         case '?':
         case '@':
         case '[':
         case '\\':
         case ']':
         case '^':
         case '_':
         case '`':
         default:
            return this.jjStartNfa_0(1, active0, active1, active2, active3, active4, active5, active6, active7, active8, active9);
         case 'A':
         case 'a':
            return this.jjMoveStringLiteralDfa3_0(active0, 264192L, active1, 2550138880L, active2, 8657043464L, active3, 576460752313057280L, active4, 1099511627782L, active5, 281474977234944L, active6, 36028797052584960L, active7, 1414599354089472L, active8, 8781824L, active9, 0L);
         case 'B':
         case 'b':
            if ((active2 & 1073741824L) != 0L) {
               return this.jjStartNfaWithStates_0(2, 158, 300);
            }

            return this.jjMoveStringLiteralDfa3_0(active0, 288230376151711744L, active1, 1099511627778L, active2, 2269391999730688L, active3, 2305843009213693954L, active4, 0L, active5, 0L, active6, 1L, active7, 32L, active8, 0L, active9, 0L);
         case 'C':
         case 'c':
            if ((active0 & 32768L) != 0L) {
               this.jjmatchedKind = 15;
               this.jjmatchedPos = 2;
            } else if ((active8 & 32768L) != 0L) {
               this.jjmatchedKind = 527;
               this.jjmatchedPos = 2;
            }

            return this.jjMoveStringLiteralDfa3_0(active0, 1073741824L, active1, -9218868437226356736L, active2, 9007474133991680L, active3, 2251804125429760L, active4, 137443147776L, active5, 1729384455933530112L, active6, -4467562034258247672L, active7, 17592190238728L, active8, 32L, active9, 0L);
         case 'D':
         case 'd':
            if ((active0 & 1024L) != 0L) {
               return this.jjStartNfaWithStates_0(2, 10, 300);
            } else if ((active0 & 4096L) != 0L) {
               return this.jjStartNfaWithStates_0(2, 12, 300);
            } else {
               if ((active0 & 2147483648L) != 0L) {
                  return this.jjStartNfaWithStates_0(2, 31, 300);
               }

               if ((active4 & 17592186044416L) != 0L) {
                  this.jjmatchedKind = 300;
                  this.jjmatchedPos = 2;
               }

               return this.jjMoveStringLiteralDfa3_0(active0, 144115188075855872L, active1, 16384L, active2, 16896L, active3, 805306368L, active4, 131072L, active5, Long.MIN_VALUE, active6, 35184372088960L, active7, -9218868432932438016L, active8, 0L, active9, 0L);
            }
         case 'E':
         case 'e':
            if ((active1 & 32768L) != 0L) {
               return this.jjStartNfaWithStates_0(2, 79, 300);
            }

            return this.jjMoveStringLiteralDfa3_0(active0, 1572864L, active1, 1125942871331072L, active2, -9223366539296636928L, active3, 206158430276L, active4, 2305843017820407296L, active5, 2252349569761288L, active6, 4810363371520L, active7, 2305843010287435792L, active8, 0L, active9, 0L);
         case 'F':
         case 'f':
            if ((active1 & 1073741824L) != 0L) {
               this.jjmatchedKind = 94;
               this.jjmatchedPos = 2;
            } else if ((active2 & 144115188075855872L) != 0L) {
               this.jjmatchedKind = 185;
               this.jjmatchedPos = 2;
            }

            return this.jjMoveStringLiteralDfa3_0(active0, 16777216L, active1, 17179869184L, active2, 64L, active3, 16384L, active4, 524320L, active5, 72057595111677952L, active6, 283673999966208L, active7, 0L, active8, 0L, active9, 0L);
         case 'G':
         case 'g':
            if ((active1 & 512L) != 0L) {
               this.jjmatchedKind = 73;
               this.jjmatchedPos = 2;
            }

            return this.jjMoveStringLiteralDfa3_0(active0, 281474976710656L, active1, 0L, active2, 2054L, active3, 0L, active4, 576531125409677568L, active5, 2305843146652648448L, active6, 0L, active7, 2147483648L, active8, 67108864L, active9, 0L);
         case 'H':
         case 'h':
            return this.jjMoveStringLiteralDfa3_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 512L, active6, 0L, active7, 1099511627780L, active8, 0L, active9, 0L);
         case 'I':
         case 'i':
            return this.jjMoveStringLiteralDfa3_0(active0, 562950490292224L, active1, 4294975488L, active2, 276824064L, active3, 4647749999818440704L, active4, 4096L, active5, 4503604190773248L, active6, 562952103002128L, active7, 288230376151711744L, active8, 4120L, active9, 0L);
         case 'J':
         case 'j':
            return this.jjMoveStringLiteralDfa3_0(active0, 0L, active1, 0L, active2, 281474976776192L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L, active9, 0L);
         case 'K':
         case 'k':
            return this.jjMoveStringLiteralDfa3_0(active0, 131941395333120L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L, active9, 0L);
         case 'L':
         case 'l':
            if ((active0 & 512L) != 0L) {
               this.jjmatchedKind = 9;
               this.jjmatchedPos = 2;
            } else if ((active4 & 4611686018427387904L) != 0L) {
               this.jjmatchedKind = 318;
               this.jjmatchedPos = 2;
            }

            return this.jjMoveStringLiteralDfa3_0(active0, 4634204016601989120L, active1, 2053641842397872273L, active2, 246842510020608L, active3, -9223363240694374136L, active4, -8935132845786005376L, active5, 4899916394646208768L, active6, 5629533893967872L, active7, 35184372088896L, active8, 0L, active9, 0L);
         case 'M':
         case 'm':
            if ((active7 & 1048576L) != 0L) {
               this.jjmatchedKind = 468;
               this.jjmatchedPos = 2;
            }

            return this.jjMoveStringLiteralDfa3_0(active0, 140737488355328L, active1, 4611686018427387912L, active2, 36591746972385280L, active3, 288230384741647408L, active4, 41944169584812032L, active5, 68719591424L, active6, 2882303761517281280L, active7, 2621440L, active8, 6291456L, active9, 0L);
         case 'N':
         case 'n':
            if ((active2 & 2305843009213693952L) != 0L) {
               return this.jjStartNfaWithStates_0(2, 189, 300);
            }

            return this.jjMoveStringLiteralDfa3_0(active0, 8388608L, active1, 144126183208910848L, active2, 0L, active3, 1152922897249993216L, active4, 9009467134836736L, active5, 18014398652088336L, active6, 1152921513205170432L, active7, 4676988230338347264L, active8, 1574913L, active9, 0L);
         case 'O':
         case 'o':
            return this.jjMoveStringLiteralDfa3_0(active0, 103079215104L, active1, 0L, active2, 128L, active3, 4508032037814400L, active4, 72057594037927936L, active5, 1099545182208L, active6, 549755814406L, active7, 571746097234433L, active8, 256L, active9, 0L);
         case 'P':
         case 'p':
            if ((active5 & 2048L) != 0L) {
               return this.jjStartNfaWithStates_0(2, 331, 300);
            }

            return this.jjMoveStringLiteralDfa3_0(active0, 2305843009213693952L, active1, 2251799813685248L, active2, 72057594037927936L, active3, 32768L, active4, 35184372088832L, active5, 8589934658L, active6, 134217792L, active7, 1024L, active8, 0L, active9, 0L);
         case 'Q':
         case 'q':
            return this.jjMoveStringLiteralDfa3_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 72057594037927936L, active7, 0L, active8, 0L, active9, 0L);
         case 'R':
         case 'r':
            if ((active0 & 17179869184L) != 0L) {
               this.jjmatchedKind = 34;
               this.jjmatchedPos = 2;
            }

            return this.jjMoveStringLiteralDfa3_0(active0, 8589934592L, active1, 72057662824513536L, active2, 5189272808098103313L, active3, 18014948265295872L, active4, 1155173306568015872L, active5, 36173932553830528L, active6, 2392538174457856L, active7, 72128375098966018L, active8, 16778240L, active9, 0L);
         case 'S':
         case 's':
            return this.jjMoveStringLiteralDfa3_0(active0, 1099981520896L, active1, 567897755747328L, active2, 17179877376L, active3, 562950089736192L, active4, 141012366262337L, active5, 43980465111040L, active6, 1091043328L, active7, 0L, active8, 514L, active9, 0L);
         case 'T':
         case 't':
            if ((active0 & 9007199254740992L) != 0L) {
               return this.jjStartNfaWithStates_0(2, 53, 300);
            }

            if ((active0 & Long.MIN_VALUE) != 0L) {
               this.jjmatchedKind = 63;
               this.jjmatchedPos = 2;
            } else if ((active7 & 4096L) != 0L) {
               this.jjmatchedKind = 460;
               this.jjmatchedPos = 2;
            } else if ((active7 & 144115188075855872L) != 0L) {
               this.jjmatchedKind = 505;
               this.jjmatchedPos = 2;
            }

            return this.jjMoveStringLiteralDfa3_0(active0, 3382102062080000L, active1, 27021598301880324L, active2, 18025428153270304L, active3, 72339069048721408L, active4, 162692536539119640L, active5, 145821907154894853L, active6, 18015566740586528L, active7, 140771848153088L, active8, 436289728L, active9, 0L);
         case 'U':
         case 'u':
            return this.jjMoveStringLiteralDfa3_0(active0, 0L, active1, 0L, active2, 1152921508901814272L, active3, 211106232532992L, active4, 0L, active5, 9007199254740992L, active6, 8192L, active7, 536870912L, active8, 8196L, active9, 0L);
         case 'V':
         case 'v':
            if ((active8 & 536870912L) != 0L) {
               return this.jjStartNfaWithStates_0(2, 541, 300);
            }

            return this.jjMoveStringLiteralDfa3_0(active0, 274877906944L, active1, 0L, active2, 4503668346847232L, active3, 1125902054328321L, active4, 0L, active5, 32L, active6, 9007199255789568L, active7, 0L, active8, 0L, active9, 0L);
         case 'W':
         case 'w':
            if ((active1 & 140737488355328L) != 0L) {
               this.jjmatchedKind = 111;
               this.jjmatchedPos = 2;
            } else if ((active5 & 16777216L) != 0L) {
               this.jjmatchedKind = 344;
               this.jjmatchedPos = 2;
            } else if ((active7 & 576460752303423488L) != 0L) {
               return this.jjStartNfaWithStates_0(2, 507, 300);
            }

            return this.jjMoveStringLiteralDfa3_0(active0, 0L, active1, 33554432L, active2, 288230376151842816L, active3, 153124586353852416L, active4, 550024251392L, active5, 131072L, active6, 70368744177664L, active7, 0L, active8, 0L, active9, 0L);
         case 'X':
         case 'x':
            return this.jjMoveStringLiteralDfa3_0(active0, 0L, active1, 281474976710720L, active2, 0L, active3, 0L, active4, 34359738368L, active5, 0L, active6, 288247989816786944L, active7, 1152921504615235584L, active8, 0L, active9, 0L);
         case 'Y':
         case 'y':
            if ((active1 & 17592186044416L) != 0L) {
               this.jjmatchedKind = 108;
               this.jjmatchedPos = 2;
            } else {
               if ((active5 & 536870912L) != 0L) {
                  return this.jjStartNfaWithStates_0(2, 349, 300);
               }

               if ((active5 & 17179869184L) != 0L) {
                  return this.jjStartNfaWithStates_0(2, 354, 300);
               }
            }

            return this.jjMoveStringLiteralDfa3_0(active0, 0L, active1, 35184372088832L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L, active9, 0L);
         case 'Z':
         case 'z':
            return this.jjMoveStringLiteralDfa3_0(active0, 0L, active1, 32L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L, active9, 0L);
         }
      }
   }

   private int jjMoveStringLiteralDfa3_0(long old0, long active0, long old1, long active1, long old2, long active2, long old3, long active3, long old4, long active4, long old5, long active5, long old6, long active6, long old7, long active7, long old8, long active8, long old9, long active9) {
      if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2) | (active3 &= old3) | (active4 &= old4) | (active5 &= old5) | (active6 &= old6) | (active7 &= old7) | (active8 &= old8) | active9 & old9) == 0L) {
         return this.jjStartNfa_0(1, old0, old1, old2, old3, old4, old5, old6, old7, old8, old9);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var42) {
            this.jjStopStringLiteralDfa_0(2, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
            return 3;
         }

         switch(this.curChar) {
         case '2':
            if ((active7 & 8192L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 461, 17);
            }
         case '3':
         case '5':
         case '6':
         case '7':
         case '9':
         case ':':
         case ';':
         case '<':
         case '=':
         case '>':
         case '?':
         case '@':
         case 'J':
         case 'Z':
         case '[':
         case '\\':
         case ']':
         case '^':
         case '`':
         case 'j':
         default:
            break;
         case '4':
            if ((active7 & 16384L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 462, 17);
            }
            break;
         case '8':
            if ((active7 & 32768L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 463, 17);
            }
            break;
         case 'A':
         case 'a':
            if ((active3 & 524288L) != 0L) {
               this.jjmatchedKind = 211;
               this.jjmatchedPos = 3;
            }

            return this.jjMoveStringLiteralDfa4_0(active0, 1091567616L, active1, 27021597797793796L, active2, 36064256286786560L, active3, 10415673652019712L, active4, 37402756251648L, active5, 1152923841069056000L, active6, 585467951759491072L, active7, 36029896531050496L, active8, 0L);
         case 'B':
         case 'b':
            if ((active7 & 8796093022208L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 491, 300);
            }

            if ((active7 & 562949953421312L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 497, 300);
            }

            return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0L, active2, 0L, active3, 8388608L, active4, 512L, active5, 1099511627776L, active6, 0L, active7, 72057594575323136L, active8, 0L);
         case 'C':
         case 'c':
            if ((active0 & 67108864L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 26, 300);
            }

            if ((active5 & 262144L) != 0L) {
               this.jjmatchedKind = 338;
               this.jjmatchedPos = 3;
            }

            return this.jjMoveStringLiteralDfa4_0(active0, 1125899907366912L, active1, 8804682956800L, active2, 8224L, active3, 4503599627370500L, active4, 1152921513196781568L, active5, 162305508447879168L, active6, 16L, active7, 413390602240L, active8, 8L);
         case 'D':
         case 'd':
            if ((active1 & 2048L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 75, 300);
            }

            if ((active2 & Long.MIN_VALUE) != 0L) {
               return this.jjStartNfaWithStates_0(3, 191, 300);
            }

            if ((active5 & 524288L) != 0L) {
               this.jjmatchedKind = 339;
               this.jjmatchedPos = 3;
            } else if ((active8 & 4096L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 524, 300);
            }

            return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0L, active2, 8657043456L, active3, 0L, active4, -9223372036854775806L, active5, 256L, active6, 70368744177664L, active7, 0L, active8, 0L);
         case 'E':
         case 'e':
            if ((active0 & 131072L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 17, 300);
            }

            if ((active0 & 268435456L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 28, 300);
            }

            if ((active0 & 8796093022208L) != 0L) {
               this.jjmatchedKind = 43;
               this.jjmatchedPos = 3;
            } else {
               if ((active1 & 8L) != 0L) {
                  return this.jjStartNfaWithStates_0(3, 67, 300);
               }

               if ((active1 & 32L) != 0L) {
                  return this.jjStartNfaWithStates_0(3, 69, 300);
               }

               if ((active3 & 256L) != 0L) {
                  return this.jjStartNfaWithStates_0(3, 200, 300);
               }

               if ((active3 & 2147483648L) != 0L) {
                  this.jjmatchedKind = 223;
                  this.jjmatchedPos = 3;
               } else {
                  if ((active3 & 17179869184L) != 0L) {
                     return this.jjStartNfaWithStates_0(3, 226, 300);
                  }

                  if ((active4 & 4503599627370496L) != 0L) {
                     this.jjmatchedKind = 308;
                     this.jjmatchedPos = 3;
                  } else {
                     if ((active4 & 9007199254740992L) != 0L) {
                        return this.jjStartNfaWithStates_0(3, 309, 300);
                     }

                     if ((active4 & 18014398509481984L) != 0L) {
                        this.jjmatchedKind = 310;
                        this.jjmatchedPos = 3;
                     } else {
                        if ((active5 & 65536L) != 0L) {
                           return this.jjStartNfaWithStates_0(3, 336, 300);
                        }

                        if ((active6 & 32L) != 0L) {
                           this.jjmatchedKind = 389;
                           this.jjmatchedPos = 3;
                        } else {
                           if ((active6 & 64L) != 0L) {
                              return this.jjStartNfaWithStates_0(3, 390, 300);
                           }

                           if ((active7 & 32L) != 0L) {
                              return this.jjStartNfaWithStates_0(3, 453, 300);
                           }
                        }
                     }
                  }
               }
            }

            return this.jjMoveStringLiteralDfa4_0(active0, 4756206934917390336L, active1, 562949970460816L, active2, 4791740531834880L, active3, 2252074993586209L, active4, 36037593112518656L, active5, -9115285643511984128L, active6, 20548112192897024L, active7, 140737633060868L, active8, 476135616L);
         case 'F':
         case 'f':
            return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0L, active2, 2560L, active3, 128L, active4, 562949953421312L, active5, 562949953421312L, active6, 2199023255552L, active7, 0L, active8, 0L);
         case 'G':
         case 'g':
            if ((active7 & 18014398509481984L) != 0L) {
               this.jjmatchedKind = 502;
               this.jjmatchedPos = 3;
            }

            return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0L, active2, 576460752303423504L, active3, 37383395344384L, active4, 576460752303423488L, active5, 2305843009213693952L, active6, 8388608L, active7, 4621819117588971520L, active8, 2048L);
         case 'H':
         case 'h':
            if ((active1 & 524288L) != 0L) {
               this.jjmatchedKind = 83;
               this.jjmatchedPos = 3;
            } else {
               if ((active4 & 8L) != 0L) {
                  return this.jjStartNfaWithStates_0(3, 259, 300);
               }

               if ((active5 & 4L) != 0L) {
                  return this.jjStartNfaWithStates_0(3, 322, 300);
               }

               if ((active6 & 1073741824L) != 0L) {
                  this.jjmatchedKind = 414;
                  this.jjmatchedPos = 3;
               }
            }

            return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0L, active2, 0L, active3, 4445962240L, active4, 67469312L, active5, 576460752303424512L, active6, 0L, active7, 0L, active8, 33554464L);
         case 'I':
         case 'i':
            return this.jjMoveStringLiteralDfa4_0(active0, 141016661491712L, active1, 2305843026393563136L, active2, 288230376187904069L, active3, 8797703635984L, active4, 140737488355332L, active5, 17592187109440L, active6, 2310382995804012552L, active7, -9218868396421021696L, active8, 2L);
         case 'K':
         case 'k':
            if ((active1 & 67108864L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 90, 300);
            }

            if ((active1 & 137438953472L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 101, 300);
            }

            if ((active1 & 4503599627370496L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 116, 300);
            }

            return this.jjMoveStringLiteralDfa4_0(active0, 562949953421312L, active1, 0L, active2, 0L, active3, 0L, active4, 137438953472L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'L':
         case 'l':
            if ((active0 & 4503599627370496L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 52, 300);
            }

            if ((active0 & 18014398509481984L) != 0L) {
               this.jjmatchedKind = 54;
               this.jjmatchedPos = 3;
            } else if ((active5 & 67108864L) != 0L) {
               this.jjmatchedKind = 346;
               this.jjmatchedPos = 3;
            } else if ((active7 & 33554432L) != 0L) {
               this.jjmatchedKind = 473;
               this.jjmatchedPos = 3;
            } else if ((active7 & 268435456L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 476, 300);
            }

            return this.jjMoveStringLiteralDfa4_0(active0, 2594073385365407744L, active1, 1408749273090L, active2, 9007199254872320L, active3, -9223372036786356216L, active4, 4224L, active5, 288230384741646338L, active6, 4611686018427387905L, active7, 288529451988288576L, active8, 0L);
         case 'M':
         case 'm':
            if ((active0 & 34359738368L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 35, 300);
            }

            if ((active8 & 4L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 514, 300);
            }

            return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 4611686087146864640L, active2, 1099511627776L, active3, 0L, active4, 8388880L, active5, 4503668346847240L, active6, 0L, active7, 2L, active8, 0L);
         case 'N':
         case 'n':
            if ((active1 & 4096L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 76, 300);
            }

            if ((active1 & 4194304L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 86, 300);
            }

            if ((active1 & 134217728L) != 0L) {
               this.jjmatchedKind = 91;
               this.jjmatchedPos = 3;
            } else {
               if ((active5 & 268435456L) != 0L) {
                  return this.jjStartNfaWithStates_0(3, 348, 300);
               }

               if ((active6 & 33554432L) != 0L) {
                  return this.jjStartNfaWithStates_0(3, 409, 300);
               }
            }

            return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 1125902324858880L, active2, 4294967296L, active3, 1297107061427077184L, active4, 70368744177664L, active5, 2251804108652544L, active6, 0L, active7, 0L, active8, 8192L);
         case 'O':
         case 'o':
            if ((active0 & 4398046511104L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 42, 300);
            }

            if ((active5 & 1L) != 0L) {
               this.jjmatchedKind = 320;
               this.jjmatchedPos = 3;
            }

            return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, -7061642016156803072L, active2, 266240L, active3, 36028805608925186L, active4, 138412032L, active5, 4611686293305295392L, active6, 140737489142016L, active7, 35184372088832L, active8, 16777216L);
         case 'P':
         case 'p':
            if ((active2 & 268435456L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 156, 300);
            }

            if ((active3 & 4194304L) != 0L) {
               this.jjmatchedKind = 214;
               this.jjmatchedPos = 3;
            } else if ((active4 & 1125899906842624L) != 0L) {
               this.jjmatchedKind = 306;
               this.jjmatchedPos = 3;
            } else {
               if ((active5 & 281474976710656L) != 0L) {
                  return this.jjStartNfaWithStates_0(3, 368, 300);
               }

               if ((active6 & 4398046511104L) != 0L) {
                  return this.jjStartNfaWithStates_0(3, 426, 300);
               }

               if ((active7 & 512L) != 0L) {
                  return this.jjStartNfaWithStates_0(3, 457, 300);
               }
            }

            return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 36028797018963968L, active2, 74872343805034496L, active3, 2594077783411916800L, active4, 285873023238144L, active5, 32768L, active6, 164352L, active7, 0L, active8, 8388608L);
         case 'Q':
         case 'q':
            return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0L, active2, 0L, active3, 4611686018427387904L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'R':
         case 'r':
            if ((active3 & 137438953472L) != 0L) {
               this.jjmatchedKind = 229;
               this.jjmatchedPos = 3;
            } else if ((active7 & 68719476736L) != 0L) {
               this.jjmatchedKind = 484;
               this.jjmatchedPos = 3;
            } else if ((active8 & 131072L) != 0L) {
               this.jjmatchedKind = 529;
               this.jjmatchedPos = 3;
            }

            return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 8388864L, active2, 137438953482L, active3, 576460821022900224L, active4, 2251799830465568L, active5, 0L, active6, 180143985094819846L, active7, 7146825580553L, active8, 263168L);
         case 'S':
         case 's':
            if ((active1 & 35184372088832L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 109, 300);
            }

            if ((active4 & 144115188075855872L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 313, 300);
            }

            if ((active4 & 2305843009213693952L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 317, 300);
            }

            if ((active5 & 131072L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 337, 300);
            }

            if ((active6 & 16777216L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 408, 300);
            }

            return this.jjMoveStringLiteralDfa4_0(active0, 545259520L, active1, 549755813952L, active2, 5764765852708634624L, active3, 140737488355328L, active4, 274878955520L, active5, 9007749044117632L, active6, 8192L, active7, 256L, active8, 0L);
         case 'T':
         case 't':
            if ((active1 & 1024L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 74, 300);
            }

            if ((active4 & 1L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 256, 300);
            }

            if ((active4 & 64L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 262, 300);
            }

            if ((active5 & 1073741824L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 350, 300);
            }

            if ((active6 & 4294967296L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 416, 300);
            }

            if ((active7 & 1152921504606846976L) != 0L) {
               this.jjmatchedKind = 508;
               this.jjmatchedPos = 3;
            }

            return this.jjMoveStringLiteralDfa4_0(active0, 134217728L, active1, 72061992084439040L, active2, 19210684348760064L, active3, 18577898218749952L, active4, 1099511627776L, active5, 13194139533312L, active6, 580544293309440L, active7, 0L, active8, 1572864L);
         case 'U':
         case 'u':
            return this.jjMoveStringLiteralDfa4_0(active0, 2251868537356288L, active1, 1114112L, active2, 586263035904L, active3, 72057594037927936L, active4, 72057594038059008L, active5, 1125899915231232L, active6, 72058143793741824L, active7, 0L, active8, 0L);
         case 'V':
         case 'v':
            return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 4294967296L, active2, 8796093022208L, active3, 34359738368L, active4, 2097152L, active5, 16L, active6, 1441151880758558720L, active7, 0L, active8, 16L);
         case 'W':
         case 'w':
            if ((active1 & 131072L) != 0L) {
               this.jjmatchedKind = 81;
               this.jjmatchedPos = 3;
            } else if ((active2 & 128L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 135, 300);
            }

            return this.jjMoveStringLiteralDfa4_0(active0, 65536L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 16L, active8, 0L);
         case 'X':
         case 'x':
            return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 2305843009213693952L, active8, 0L);
         case 'Y':
         case 'y':
            if ((active4 & 288230376151711744L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 314, 300);
            }

            if ((active6 & 1125899906842624L) != 0L) {
               return this.jjStartNfaWithStates_0(3, 434, 300);
            }

            return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, -9223363240761753600L, active7, 2322185737732096L, active8, 513L);
         case '_':
            return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 2533274790395905L, active2, 0L, active3, 0L, active4, 659009044480L, active5, 0L, active6, 128L, active7, 0L, active8, 256L);
         }

         return this.jjStartNfa_0(2, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
      }
   }

   private int jjMoveStringLiteralDfa4_0(long old0, long active0, long old1, long active1, long old2, long active2, long old3, long active3, long old4, long active4, long old5, long active5, long old6, long active6, long old7, long active7, long old8, long active8) {
      if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2) | (active3 &= old3) | (active4 &= old4) | (active5 &= old5) | (active6 &= old6) | (active7 &= old7) | (active8 &= old8)) == 0L) {
         return this.jjStartNfa_0(2, old0, old1, old2, old3, old4, old5, old6, old7, old8, 0L);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var38) {
            this.jjStopStringLiteralDfa_0(3, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
            return 4;
         }

         switch(this.curChar) {
         case ' ':
            return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 98304L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case '!':
         case '"':
         case '#':
         case '$':
         case '%':
         case '&':
         case '\'':
         case '(':
         case ')':
         case '*':
         case '+':
         case ',':
         case '-':
         case '.':
         case '/':
         case '0':
         case '1':
         case '3':
         case '5':
         case '6':
         case '7':
         case '8':
         case '9':
         case ':':
         case ';':
         case '<':
         case '=':
         case '>':
         case '?':
         case '@':
         case 'J':
         case 'Q':
         case 'Z':
         case '[':
         case '\\':
         case ']':
         case '^':
         case '`':
         case 'j':
         case 'q':
         default:
            break;
         case '2':
            if ((active0 & 35184372088832L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 45, 17);
            }
            break;
         case '4':
            if ((active0 & 70368744177664L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 46, 17);
            }
            break;
         case 'A':
         case 'a':
            if ((active8 & 65536L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 528, 300);
            }

            return this.jjMoveStringLiteralDfa5_0(active0, 2305843009213693952L, active1, 36037593111986176L, active2, 2251937253171201L, active3, 2306405959184154624L, active4, -9223372036837998576L, active5, 4539883511087104L, active6, 1441151880758558722L, active7, 549755813890L, active8, 48L);
         case 'B':
         case 'b':
            if ((active7 & 35184372088832L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 493, 300);
            }

            return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 4L, active2, 0L, active3, 2097152L, active4, 536871040L, active5, 0L, active6, 0L, active7, 11258999068426240L, active8, 0L);
         case 'C':
         case 'c':
            if ((active0 & 17592186044416L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 44, 300);
            }

            return this.jjMoveStringLiteralDfa5_0(active0, 4611686018427387904L, active1, 536870913L, active2, 422212469325824L, active3, 70368744185856L, active4, 8796093153280L, active5, 1152921504606846976L, active6, -9223363240761753600L, active7, 6597069766656L, active8, 2048L);
         case 'D':
         case 'd':
            if ((active2 & 288230376151711744L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 186, 300);
            }

            if ((active4 & 4096L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 268, 300);
            }

            if ((active7 & 8388608L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 471, 300);
            }

            return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 34359738368L, active2, 1024L, active3, 0L, active4, 4202496L, active5, 0L, active6, 4503599627370496L, active7, 1L, active8, 0L);
         case 'E':
         case 'e':
            if ((active0 & 562949953421312L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 49, 300);
            }

            if ((active1 & 2L) != 0L) {
               this.jjmatchedKind = 65;
               this.jjmatchedPos = 4;
            } else {
               if ((active1 & 8388608L) != 0L) {
                  return this.jjStartNfaWithStates_0(4, 87, 300);
               }

               if ((active2 & 16L) != 0L) {
                  return this.jjStartNfaWithStates_0(4, 132, 300);
               }

               if ((active2 & 2147483648L) != 0L) {
                  this.jjmatchedKind = 159;
                  this.jjmatchedPos = 4;
               } else {
                  if ((active2 & 576460752303423488L) != 0L) {
                     return this.jjStartNfaWithStates_0(4, 187, 300);
                  }

                  if ((active2 & 1152921504606846976L) != 0L) {
                     return this.jjStartNfaWithStates_0(4, 188, 300);
                  }

                  if ((active3 & 4398046511104L) != 0L) {
                     return this.jjStartNfaWithStates_0(4, 234, 300);
                  }

                  if ((active5 & 140737488355328L) != 0L) {
                     return this.jjStartNfaWithStates_0(4, 367, 300);
                  }

                  if ((active5 & 576460752303423488L) != 0L) {
                     return this.jjStartNfaWithStates_0(4, 379, 300);
                  }

                  if ((active6 & 4L) != 0L) {
                     return this.jjStartNfaWithStates_0(4, 386, 300);
                  }

                  if ((active6 & 8388608L) != 0L) {
                     return this.jjStartNfaWithStates_0(4, 407, 300);
                  }

                  if ((active6 & 268435456L) != 0L) {
                     return this.jjStartNfaWithStates_0(4, 412, 300);
                  }

                  if ((active6 & 536870912L) != 0L) {
                     return this.jjStartNfaWithStates_0(4, 413, 300);
                  }

                  if ((active6 & 4611686018427387904L) != 0L) {
                     return this.jjStartNfaWithStates_0(4, 446, 300);
                  }

                  if ((active7 & 1125899906842624L) != 0L) {
                     return this.jjStartNfaWithStates_0(4, 498, 300);
                  }

                  if ((active7 & 288230376151711744L) != 0L) {
                     return this.jjStartNfaWithStates_0(4, 506, 300);
                  }
               }
            }

            return this.jjMoveStringLiteralDfa5_0(active0, 65536L, active1, 72062266962411520L, active2, 9912792907778L, active3, 1153062280749907972L, active4, 1731704425477570848L, active5, 549755822104L, active6, 216245349881217025L, active7, 17301504L, active8, 1024L);
         case 'F':
         case 'f':
            return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0L, active2, 0L, active3, 281544233058304L, active4, 282024732524544L, active5, 288230376151711744L, active6, 0L, active7, 0L, active8, 16777216L);
         case 'G':
         case 'g':
            if ((active5 & 4294967296L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 352, 300);
            }

            return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 128L, active2, 4096L, active3, 35184372285952L, active4, 140737488355328L, active5, 4611686018427387904L, active6, 0L, active7, 2048L, active8, 258L);
         case 'H':
         case 'h':
            if ((active5 & 2097152L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 341, 300);
            }

            if ((active5 & 144115188075855872L) != 0L) {
               this.jjmatchedKind = 377;
               this.jjmatchedPos = 4;
            } else if ((active8 & 1048576L) != 0L) {
               this.jjmatchedKind = 532;
               this.jjmatchedPos = 4;
            }

            return this.jjMoveStringLiteralDfa5_0(active0, 1125899906842624L, active1, 0L, active2, 18014398509482016L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 412316860416L, active8, 8912904L);
         case 'I':
         case 'i':
            if ((active6 & 8L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 387, 300);
            }

            return this.jjMoveStringLiteralDfa5_0(active0, 288230384875864064L, active1, 4611687740709273664L, active2, 4684869512372292096L, active3, 27022147654254720L, active4, 564051612534786L, active5, 2341876272998645762L, active6, 580544287016064L, active7, 72127981035717632L, active8, 33554432L);
         case 'K':
         case 'k':
            if ((active0 & 524288L) != 0L) {
               this.jjmatchedKind = 19;
               this.jjmatchedPos = 4;
            } else {
               if ((active2 & 4294967296L) != 0L) {
                  return this.jjStartNfaWithStates_0(4, 160, 300);
               }

               if ((active3 & 4503599627370496L) != 0L) {
                  return this.jjStartNfaWithStates_0(4, 244, 300);
               }
            }

            return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0L, active2, 0L, active3, 2048L, active4, 8589934592L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'L':
         case 'l':
            if ((active4 & 2097152L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 277, 300);
            }

            if ((active5 & 2199023255552L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 361, 300);
            }

            return this.jjMoveStringLiteralDfa5_0(active0, 140737488355328L, active1, 17179869184L, active2, 563018672898048L, active3, 289356276067991552L, active4, 16388L, active5, 18014398509482752L, active6, 11259411452395520L, active7, 281484170625024L, active8, 0L);
         case 'M':
         case 'm':
            return this.jjMoveStringLiteralDfa5_0(active0, 4299161600L, active1, Long.MIN_VALUE, active2, 4504149402058752L, active3, 1099511627776L, active4, 2199023255552L, active5, 0L, active6, 1133871628288L, active7, 4194308L, active8, 0L);
         case 'N':
         case 'n':
            if ((active1 & 8192L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 77, 300);
            }

            if ((active3 & 16L) != 0L) {
               this.jjmatchedKind = 196;
               this.jjmatchedPos = 4;
            }

            return this.jjMoveStringLiteralDfa5_0(active0, 274878169088L, active1, 2199023255568L, active2, 2199023255620L, active3, 8623490080L, active4, 0L, active5, 137438953472L, active6, 2305843077933187072L, active7, 2147483648L, active8, 0L);
         case 'O':
         case 'o':
            return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0L, active2, 33554688L, active3, 8L, active4, 262144L, active5, 17592187125760L, active6, 16L, active7, 17626545782784L, active8, 0L);
         case 'P':
         case 'p':
            if ((active0 & 68719476736L) != 0L) {
               this.jjmatchedKind = 36;
               this.jjmatchedPos = 4;
            }

            return this.jjMoveStringLiteralDfa5_0(active0, 1073741824L, active1, 18014398509481984L, active2, 36033195065475072L, active3, 2251799813685249L, active4, 72057594306363392L, active5, 4096L, active6, 549755813888L, active7, 0L, active8, 0L);
         case 'R':
         case 'r':
            if ((active0 & 8192L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 13, 300);
            }

            if ((active0 & 144115188075855872L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 57, 300);
            }

            if ((active3 & 4096L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 204, 300);
            }

            if ((active3 & 274877906944L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 230, 300);
            }

            if ((active3 & 36028797018963968L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 247, 300);
            }

            if ((active5 & 134217728L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 347, 300);
            }

            if ((active5 & 2147483648L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 351, 300);
            }

            if ((active7 & 1099511627776L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 488, 300);
            }

            return this.jjMoveStringLiteralDfa5_0(active0, 2252899325313024L, active1, 156218612092502272L, active2, 34360000512L, active3, 72057594037944320L, active4, 39689927524352L, active5, 72629340088565760L, active6, 18295873628406016L, active7, 36028797021061120L, active8, 16576L);
         case 'S':
         case 's':
            if ((active2 & 8589934592L) != 0L) {
               this.jjmatchedKind = 161;
               this.jjmatchedPos = 4;
            } else {
               if ((active3 & Long.MIN_VALUE) != 0L) {
                  return this.jjStartNfaWithStates_0(4, 255, 300);
               }

               if ((active5 & 8388608L) != 0L) {
                  return this.jjStartNfaWithStates_0(4, 343, 300);
               }

               if ((active5 & 33554432L) != 0L) {
                  return this.jjStartNfaWithStates_0(4, 345, 300);
               }

               if ((active8 & 512L) != 0L) {
                  return this.jjStartNfaWithStates_0(4, 521, 300);
               }

               if ((active8 & 262144L) != 0L) {
                  return this.jjStartNfaWithStates_0(4, 530, 300);
               }
            }

            return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 2305843009482129408L, active2, 67158024L, active3, 0L, active4, 36028798092705792L, active5, 1125899906842624L, active6, 36028797018963968L, active7, 0L, active8, 4194304L);
         case 'T':
         case 't':
            if ((active1 & 2147483648L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 95, 300);
            }

            if ((active3 & 8796093022208L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 235, 300);
            }

            if ((active3 & 576460752303423488L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 251, 300);
            }

            if ((active5 & 32L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 325, 300);
            }

            if ((active5 & 128L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 327, 300);
            }

            if ((active5 & 1024L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 330, 300);
            }

            if ((active5 & 16384L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 334, 300);
            }

            if ((active6 & 35184372088832L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 429, 300);
            }

            if ((active7 & 65536L) != 0L) {
               this.jjmatchedKind = 464;
               this.jjmatchedPos = 4;
            } else if ((active7 & 2305843009213693952L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 509, 300);
            }

            return this.jjMoveStringLiteralDfa5_0(active0, 579862528L, active1, 1125899906859008L, active2, 53051437088768L, active3, 1140850752L, active4, 0L, active5, 11258999068426240L, active6, 8192L, active7, 4611826755916136704L, active8, 404758529L);
         case 'U':
         case 'u':
            return this.jjMoveStringLiteralDfa5_0(active0, 16777216L, active1, 8589934592L, active2, 9007199254740992L, active3, 4755803405526532098L, active4, 512L, active5, 0L, active6, 32768L, active7, -9218868432932437952L, active8, 0L);
         case 'V':
         case 'v':
            return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 64L, active6, 0L, active7, 0L, active8, 0L);
         case 'W':
         case 'w':
            return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 2017612633061982208L, active2, 0L, active3, 0L, active4, 274877906944L, active5, 0L, active6, 140737488355328L, active7, 0L, active8, 0L);
         case 'X':
         case 'x':
            if ((active5 & Long.MIN_VALUE) != 0L) {
               this.jjmatchedKind = 383;
               this.jjmatchedPos = 4;
            }

            return this.jjMoveStringLiteralDfa5_0(active0, 281474976710656L, active1, 2097152L, active2, 0L, active3, 268435456L, active4, 0L, active5, 0L, active6, 576460778073227264L, active7, 0L, active8, 67108864L);
         case 'Y':
         case 'y':
            if ((active4 & 1024L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 266, 300);
            }

            if ((active5 & 8589934592L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 353, 300);
            }

            if ((active7 & 134217728L) != 0L) {
               return this.jjStartNfaWithStates_0(4, 475, 300);
            }

            return this.jjMoveStringLiteralDfa5_0(active0, 2048L, active1, 33554432L, active2, 70368744177664L, active3, 0L, active4, 17213423616L, active5, 0L, active6, 0L, active7, 8L, active8, 0L);
         case '_':
            return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0L, active2, 134217728L, active3, 0L, active4, 137506062336L, active5, 274877906944L, active6, 2560L, active7, 16L, active8, 0L);
         }

         return this.jjStartNfa_0(3, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
      }
   }

   private int jjMoveStringLiteralDfa5_0(long old0, long active0, long old1, long active1, long old2, long active2, long old3, long active3, long old4, long active4, long old5, long active5, long old6, long active6, long old7, long active7, long old8, long active8) {
      if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2) | (active3 &= old3) | (active4 &= old4) | (active5 &= old5) | (active6 &= old6) | (active7 &= old7) | (active8 &= old8)) == 0L) {
         return this.jjStartNfa_0(3, old0, old1, old2, old3, old4, old5, old6, old7, old8, 0L);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var38) {
            this.jjStopStringLiteralDfa_0(4, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
            return 5;
         }

         switch(this.curChar) {
         case '4':
            if ((active7 & 131072L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 465, 17);
            }
         case '5':
         case '6':
         case '7':
         case '9':
         case ':':
         case ';':
         case '<':
         case '=':
         case '>':
         case '?':
         case '@':
         case 'J':
         case 'Q':
         case 'X':
         case '[':
         case '\\':
         case ']':
         case '^':
         case '`':
         case 'j':
         case 'q':
         case 'x':
         default:
            break;
         case '8':
            if ((active7 & 262144L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 466, 17);
            }
            break;
         case 'A':
         case 'a':
            return this.jjMoveStringLiteralDfa6_0(active0, 2392541597007872L, active1, 18014398794694677L, active2, 562949953421312L, active3, 70368744185856L, active4, 35184372088960L, active5, 288234774198222848L, active6, 5067651380412416L, active7, 412337831940L, active8, 8L);
         case 'B':
         case 'b':
            if ((active7 & 17592186044416L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 492, 300);
            }

            return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0L, active2, 524288L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'C':
         case 'c':
            if ((active1 & 1099511627776L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 104, 300);
            }

            if ((active4 & 1099511627776L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 296, 300);
            }

            return this.jjMoveStringLiteralDfa6_0(active0, 2305843009213693952L, active1, 274877906944L, active2, 134234112L, active3, 1152921508901814272L, active4, 0L, active5, 2L, active6, 68719476736L, active7, 549755814912L, active8, 0L);
         case 'D':
         case 'd':
            if ((active2 & 17179869184L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 162, 300);
            }

            if ((active3 & 140737488355328L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 239, 300);
            }

            if ((active4 & 576460752303423488L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 315, 300);
            }

            return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 72057594037927936L, active2, 9007199255003136L, active3, 4L, active4, 70368744177664L, active5, 35184372089344L, active6, 140737488355344L, active7, 281474976710656L, active8, 0L);
         case 'E':
         case 'e':
            if ((active0 & 1048576L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 20, 300);
            }

            if ((active0 & 33554432L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 25, 300);
            }

            if ((active0 & 1073741824L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 30, 300);
            }

            if ((active1 & 16384L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 78, 300);
            }

            if ((active1 & 17179869184L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 98, 300);
            }

            if ((active2 & 2L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 129, 300);
            }

            if ((active2 & 4L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 130, 300);
            }

            if ((active2 & 64L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 134, 300);
            }

            if ((active3 & 512L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 201, 300);
            }

            if ((active3 & 2048L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 203, 300);
            }

            if ((active3 & 16384L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 206, 300);
            }

            if ((active3 & 65536L) != 0L) {
               this.jjmatchedKind = 208;
               this.jjmatchedPos = 5;
            } else {
               if ((active3 & 8388608L) != 0L) {
                  return this.jjStartNfaWithStates_0(5, 215, 300);
               }

               if ((active3 & 1099511627776L) != 0L) {
                  return this.jjStartNfaWithStates_0(5, 232, 300);
               }

               if ((active3 & 288230376151711744L) != 0L) {
                  return this.jjStartNfaWithStates_0(5, 250, 300);
               }

               if ((active3 & 4611686018427387904L) != 0L) {
                  return this.jjStartNfaWithStates_0(5, 254, 300);
               }

               if ((active4 & 4194304L) != 0L) {
                  return this.jjStartNfaWithStates_0(5, 278, 300);
               }

               if ((active4 & 134217728L) != 0L) {
                  this.jjmatchedKind = 283;
                  this.jjmatchedPos = 5;
               } else {
                  if ((active6 & 16384L) != 0L) {
                     return this.jjStartNfaWithStates_0(5, 398, 300);
                  }

                  if ((active6 & 36028797018963968L) != 0L) {
                     return this.jjStartNfaWithStates_0(5, 439, 300);
                  }

                  if ((active7 & 536870912L) != 0L) {
                     return this.jjStartNfaWithStates_0(5, 477, 300);
                  }
               }
            }

            return this.jjMoveStringLiteralDfa6_0(active0, 1125899907170304L, active1, 144115188075855872L, active2, 4503599644147752L, active3, 35184439328768L, active4, 4398080212992L, active5, 82753643152932864L, active6, 140032L, active7, 4611686018427389953L, active8, 8449L);
         case 'F':
         case 'f':
            return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0L, active2, 5120L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'G':
         case 'g':
            if ((active0 & 274877906944L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 38, 300);
            }

            return this.jjMoveStringLiteralDfa6_0(active0, 8589934592L, active1, 36028797018963968L, active2, 0L, active3, 0L, active4, 2147483648L, active5, 4647714815446351872L, active6, 2L, active7, 0L, active8, 0L);
         case 'H':
         case 'h':
            return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0L, active2, 140737488355328L, active3, 0L, active4, 0L, active5, 1152921504606846976L, active6, 0L, active7, 6597069766656L, active8, 2048L);
         case 'I':
         case 'i':
            return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 1125899907105024L, active2, 52776628387840L, active3, 1409582496744512L, active4, 72341268574773764L, active5, 2261008223567872L, active6, 9007645931341824L, active7, 140746080387072L, active8, 427819008L);
         case 'K':
         case 'k':
            return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 137438953472L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'L':
         case 'l':
            if ((active5 & 1099511627776L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 360, 300);
            }

            return this.jjMoveStringLiteralDfa6_0(active0, 16777216L, active1, 4294967296L, active2, 2561L, active3, 68721574016L, active4, 562949953421312L, active5, 0L, active6, -7782211359936086016L, active7, 11258999068426240L, active8, 0L);
         case 'M':
         case 'm':
            if ((active1 & 4398046511104L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 106, 300);
            }

            if ((active3 & 144115188075855872L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 249, 300);
            }

            return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, -9223372036852678656L, active2, 0L, active3, 0L, active4, 1073741824L, active5, 0L, active6, 144132780261900288L, active7, -9218868432865329136L, active8, 0L);
         case 'N':
         case 'n':
            if ((active0 & 4194304L) != 0L) {
               this.jjmatchedKind = 22;
               this.jjmatchedPos = 5;
            } else if ((active2 & 34359738368L) != 0L) {
               this.jjmatchedKind = 163;
               this.jjmatchedPos = 5;
            } else if ((active5 & 1048576L) != 0L) {
               this.jjmatchedKind = 340;
               this.jjmatchedPos = 5;
            } else {
               if ((active5 & 17592186044416L) != 0L) {
                  return this.jjStartNfaWithStates_0(5, 364, 300);
               }

               if ((active8 & 33554432L) != 0L) {
                  return this.jjStartNfaWithStates_0(5, 537, 300);
               }
            }

            return this.jjMoveStringLiteralDfa6_0(active0, 288230376285929472L, active1, 68719476736L, active2, 72059243339055104L, active3, 72620544142344194L, active4, 1155314041917276418L, active5, 2305843009213693960L, active6, 90071992547410048L, active7, 72128014321713152L, active8, 1026L);
         case 'O':
         case 'o':
            return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 2317384033551515648L, active2, 4611690691351805952L, active3, 1074790401L, active4, 932007903232L, active5, 320L, active6, 0L, active7, 0L, active8, 0L);
         case 'P':
         case 'p':
            if ((active0 & 281474976710656L) != 0L) {
               this.jjmatchedKind = 48;
               this.jjmatchedPos = 5;
            } else {
               if ((active4 & 16L) != 0L) {
                  return this.jjStartNfaWithStates_0(5, 260, 300);
               }

               if ((active7 & 64L) != 0L) {
                  return this.jjStartNfaWithStates_0(5, 454, 300);
               }
            }

            return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0L, active2, 36099165763141632L, active3, 0L, active4, 67141632L, active5, 0L, active6, 70368744439808L, active7, 8L, active8, 67108864L);
         case 'R':
         case 'r':
            if ((active7 & 524288L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 467, 300);
            }

            if ((active8 & 32L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 517, 300);
            }

            return this.jjMoveStringLiteralDfa6_0(active0, 8388608L, active1, 128L, active2, 20274994416197632L, active3, 2305843043573465088L, active4, 269025280L, active5, 4504149383217168L, active6, 283673999966208L, active7, 256L, active8, 16L);
         case 'S':
         case 's':
            if ((active0 & 536870912L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 29, 300);
            }

            if ((active1 & 65536L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 80, 300);
            }

            if ((active1 & 33554432L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 89, 300);
            }

            if ((active1 & 34359738368L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 99, 300);
            }

            if ((active2 & 32768L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 143, 300);
            }

            if ((active2 & 68719476736L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 164, 300);
            }

            if ((active6 & 549755813888L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 423, 300);
            }

            if ((active8 & 524288L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 531, 300);
            }

            return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 1048576L, active2, 8388864L, active3, 32L, active4, 8589934624L, active5, 4194304L, active6, 1115137L, active7, 1073741824L, active8, 0L);
         case 'T':
         case 't':
            if ((active0 & 1099511627776L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 40, 300);
            }

            if ((active0 & 4611686018427387904L) != 0L) {
               this.jjmatchedKind = 62;
               this.jjmatchedPos = 5;
            } else {
               if ((active1 & 8796093022208L) != 0L) {
                  return this.jjStartNfaWithStates_0(5, 107, 300);
               }

               if ((active2 & 65536L) != 0L) {
                  return this.jjStartNfaWithStates_0(5, 144, 300);
               }

               if ((active2 & 281474976710656L) != 0L) {
                  return this.jjStartNfaWithStates_0(5, 176, 300);
               }

               if ((active3 & 33554432L) != 0L) {
                  return this.jjStartNfaWithStates_0(5, 217, 300);
               }

               if ((active3 & 9007199254740992L) != 0L) {
                  return this.jjStartNfaWithStates_0(5, 245, 300);
               }

               if ((active5 & 4096L) != 0L) {
                  this.jjmatchedKind = 332;
                  this.jjmatchedPos = 5;
               } else {
                  if ((active5 & 8192L) != 0L) {
                     return this.jjStartNfaWithStates_0(5, 333, 300);
                  }

                  if ((active5 & 68719476736L) != 0L) {
                     this.jjmatchedKind = 356;
                     this.jjmatchedPos = 5;
                  } else {
                     if ((active6 & 524288L) != 0L) {
                        return this.jjStartNfaWithStates_0(5, 403, 300);
                     }

                     if ((active7 & 2L) != 0L) {
                        return this.jjStartNfaWithStates_0(5, 449, 300);
                     }

                     if ((active7 & 2147483648L) != 0L) {
                        return this.jjStartNfaWithStates_0(5, 479, 300);
                     }
                  }
               }
            }

            return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 4611686027017322496L, active2, 1128098934292480L, active3, 20266748347678720L, active4, -9187334443726010368L, active5, 0L, active6, 25769836544L, active7, 0L, active8, 4194304L);
         case 'U':
         case 'u':
            return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 262144L, active5, 18014398509481984L, active6, 0L, active7, 0L, active8, 0L);
         case 'V':
         case 'v':
            return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 562949953421312L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 2882303761517117440L, active7, 0L, active8, 16576L);
         case 'W':
         case 'w':
            return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0L, active2, 0L, active3, 8L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'Y':
         case 'y':
            if ((active2 & 137438953472L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 165, 300);
            }

            if ((active3 & 536870912L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 221, 300);
            }

            if ((active6 & 2251799813685248L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 435, 300);
            }

            if ((active7 & 36028797018963968L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 503, 300);
            }

            return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 2199023255552L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'Z':
         case 'z':
            if ((active8 & 2097152L) != 0L) {
               return this.jjStartNfaWithStates_0(5, 533, 300);
            }

            return this.jjMoveStringLiteralDfa6_0(active0, 2048L, active1, 64L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case '_':
            return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 2017612633061982208L, active2, 0L, active3, 0L, active4, 17180917760L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         }

         return this.jjStartNfa_0(4, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
      }
   }

   private int jjMoveStringLiteralDfa6_0(long old0, long active0, long old1, long active1, long old2, long active2, long old3, long active3, long old4, long active4, long old5, long active5, long old6, long active6, long old7, long active7, long old8, long active8) {
      if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2) | (active3 &= old3) | (active4 &= old4) | (active5 &= old5) | (active6 &= old6) | (active7 &= old7) | (active8 &= old8)) == 0L) {
         return this.jjStartNfa_0(4, old0, old1, old2, old3, old4, old5, old6, old7, old8, 0L);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var38) {
            this.jjStopStringLiteralDfa_0(5, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
            return 6;
         }

         switch(this.curChar) {
         case ' ':
            return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 70368744177664L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case '!':
         case '"':
         case '#':
         case '$':
         case '%':
         case '&':
         case '\'':
         case '(':
         case ')':
         case '*':
         case '+':
         case ',':
         case '-':
         case '.':
         case '/':
         case '0':
         case '1':
         case '2':
         case '3':
         case '4':
         case '5':
         case '6':
         case '7':
         case '8':
         case '9':
         case ':':
         case ';':
         case '<':
         case '=':
         case '>':
         case '?':
         case '@':
         case 'J':
         case 'Q':
         case '[':
         case '\\':
         case ']':
         case '^':
         case '`':
         case 'j':
         case 'q':
         default:
            return this.jjStartNfa_0(5, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
         case 'A':
         case 'a':
            if ((active4 & Long.MIN_VALUE) != 0L) {
               return this.jjStartNfaWithStates_0(6, 319, 300);
            }

            return this.jjMoveStringLiteralDfa7_0(active0, 8388608L, active1, 68719738880L, active2, 167788544L, active3, 0L, active4, 36028798092713984L, active5, 32768L, active6, 2900617227189354496L, active7, 72345700444144896L, active8, 4212928L);
         case 'B':
         case 'b':
            return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 4503599627370500L, active8, 0L);
         case 'C':
         case 'c':
            if ((active4 & 2199023255552L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 297, 300);
            } else if ((active7 & 2097152L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 469, 300);
            } else {
               if ((active8 & 8388608L) != 0L) {
                  return this.jjStartNfaWithStates_0(6, 535, 300);
               }

               return this.jjMoveStringLiteralDfa7_0(active0, 134217728L, active1, 268435456L, active2, 0L, active3, 562949954469888L, active4, 128L, active5, 288239172244734208L, active6, 72057594037927936L, active7, 0L, active8, 1040L);
            }
         case 'D':
         case 'd':
            if ((active0 & 262144L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 18, 300);
            } else if ((active2 & 32L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 133, 300);
            } else if ((active4 & 131072L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 273, 300);
            } else if ((active4 & 33554432L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 281, 300);
            } else {
               if ((active5 & 1125899906842624L) != 0L) {
                  return this.jjStartNfaWithStates_0(6, 370, 300);
               }

               return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 288230376151712000L, active2, 8192L, active3, 1128098930098178L, active4, 0L, active5, 0L, active6, 9007199254741120L, active7, 0L, active8, 0L);
            }
         case 'E':
         case 'e':
            if ((active0 & 2048L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 11, 300);
            } else if ((active0 & 2305843009213693952L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 61, 300);
            } else if ((active1 & 64L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 70, 300);
            } else if ((active1 & 8589934592L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 97, 300);
            } else if ((active2 & 512L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 137, 300);
            } else if ((active2 & 2048L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 139, 300);
            } else if ((active2 & 131072L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 145, 300);
            } else if ((active2 & 70368744177664L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 174, 300);
            } else if ((active3 & 128L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 199, 300);
            } else if ((active3 & 32768L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 207, 300);
            } else if ((active3 & 262144L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 210, 300);
            } else if ((active3 & 2097152L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 213, 300);
            } else if ((active4 & 2048L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 267, 300);
            } else if ((active4 & 562949953421312L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 305, 300);
            } else if ((active5 & 35184372088832L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 365, 300);
            } else if ((active5 & 562949953421312L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 369, 300);
            } else if ((active5 & 1152921504606846976L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 380, 300);
            } else if ((active6 & 2L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 385, 300);
            } else if ((active6 & 16L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 388, 300);
            } else if ((active6 & 32768L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 399, 300);
            } else if ((active6 & 1048576L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 404, 300);
            } else if ((active6 & 8796093022208L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 427, 300);
            } else {
               if ((active6 & Long.MIN_VALUE) != 0L) {
                  return this.jjStartNfaWithStates_0(6, 447, 300);
               }

               return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 36591751267352576L, active2, 18155135997837568L, active3, 0L, active4, 140874927833088L, active5, 4194304L, active6, 144326320145301504L, active7, 16L, active8, 2L);
            }
         case 'F':
         case 'f':
            return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0L, active2, 0L, active3, 34359738432L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'G':
         case 'g':
            if ((active2 & 72057594037927936L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 184, 300);
            } else if ((active3 & 134217728L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 219, 300);
            } else if ((active4 & 2L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 257, 300);
            } else if ((active5 & 2305843009213693952L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 381, 300);
            } else {
               if ((active7 & 70368744177664L) != 0L) {
                  return this.jjStartNfaWithStates_0(6, 494, 300);
               }

               return this.jjMoveStringLiteralDfa7_0(active0, 288230376151711744L, active1, 18014398509481984L, active2, 0L, active3, 16777216L, active4, 536870912L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
         case 'H':
         case 'h':
            if ((active4 & 32L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 261, 300);
            }

            return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 2147483648L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'I':
         case 'i':
            return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, -9223372036853727232L, active2, 46164095203808257L, active3, 92324342116909101L, active4, 268435456L, active5, 4611686018427387906L, active6, 0L, active7, 5368709120L, active8, 0L);
         case 'K':
         case 'k':
            if ((active3 & 4294967296L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 224, 300);
            }

            return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 17179869184L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'L':
         case 'l':
            if ((active0 & 2251799813685248L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 51, 300);
            } else if ((active1 & 2097152L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 85, 300);
            } else if ((active5 & 4398046511104L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 362, 300);
            } else {
               if ((active6 & 2147483648L) != 0L) {
                  this.jjmatchedKind = 415;
                  this.jjmatchedPos = 6;
               } else {
                  if ((active6 & 1099511627776L) != 0L) {
                     return this.jjStartNfaWithStates_0(6, 424, 300);
                  }

                  if ((active7 & 4194304L) != 0L) {
                     return this.jjStartNfaWithStates_0(6, 470, 300);
                  }
               }

               return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 2305843009213693953L, active2, 524288L, active3, 281474976710656L, active4, 281474976711168L, active5, 512L, active6, 562950087639040L, active7, 0L, active8, 16777216L);
            }
         case 'M':
         case 'm':
            if ((active1 & 2199023255552L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 105, 300);
            }

            return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 536870928L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 140737488355328L, active8, 402653184L);
         case 'N':
         case 'n':
            if ((active0 & 65536L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 16, 300);
            } else if ((active1 & 549755813888L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 103, 300);
            } else {
               if ((active5 & 36028797018963968L) != 0L) {
                  this.jjmatchedKind = 375;
                  this.jjmatchedPos = 6;
               } else if ((active7 & 16777216L) != 0L) {
                  return this.jjStartNfaWithStates_0(6, 472, 300);
               }

               return this.jjMoveStringLiteralDfa7_0(active0, 8589934592L, active1, 0L, active2, 4616189618054758400L, active3, 0L, active4, 72057594037927940L, active5, 72057868915834880L, active6, 6293504L, active7, 8589934592L, active8, 0L);
            }
         case 'O':
         case 'o':
            return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 128L, active2, 39582423842816L, active3, 68719476736L, active4, 16842752L, active5, 0L, active6, 0L, active7, 11258999135535104L, active8, 256L);
         case 'P':
         case 'p':
            return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 1152921504606846976L, active2, 8388608L, active3, 0L, active4, 0L, active5, 0L, active6, 1L, active7, 0L, active8, 0L);
         case 'R':
         case 'r':
            if ((active2 & 274877906944L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 166, 300);
            } else if ((active3 & 35184372088832L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 237, 300);
            } else {
               if ((active6 & 8192L) != 0L) {
                  this.jjmatchedKind = 397;
                  this.jjmatchedPos = 6;
               } else {
                  if ((active7 & 1L) != 0L) {
                     return this.jjStartNfaWithStates_0(6, 448, 300);
                  }

                  if ((active7 & 2048L) != 0L) {
                     return this.jjStartNfaWithStates_0(6, 459, 300);
                  }

                  if ((active7 & 137438953472L) != 0L) {
                     this.jjmatchedKind = 485;
                     this.jjmatchedPos = 6;
                  } else {
                     if ((active8 & 8L) != 0L) {
                        return this.jjStartNfaWithStates_0(6, 515, 300);
                     }

                     if ((active8 & 8192L) != 0L) {
                        return this.jjStartNfaWithStates_0(6, 525, 300);
                     }
                  }
               }

               return this.jjMoveStringLiteralDfa7_0(active0, 140737488355328L, active1, 576460752303423488L, active2, 0L, active3, 1073741824L, active4, 824701911040L, active5, 9007199254740992L, active6, 68719738880L, active7, 274877906944L, active8, 0L);
            }
         case 'S':
         case 's':
            if ((active0 & 1125899906842624L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 50, 300);
            } else if ((active2 & 262144L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 146, 300);
            } else if ((active2 & 549755813888L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 167, 300);
            } else {
               if ((active3 & 131072L) != 0L) {
                  return this.jjStartNfaWithStates_0(6, 209, 300);
               }

               return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 4L, active2, 8796093022208L, active3, 1024L, active4, 4398046511104L, active5, 18014398509481984L, active6, 412316991488L, active7, 0L, active8, 0L);
            }
         case 'T':
         case 't':
            if ((active0 & 16777216L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 24, 300);
            } else if ((active1 & 274877906944L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 102, 300);
            } else if ((active2 & 8L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 131, 300);
            } else {
               if ((active2 & 1099511627776L) != 0L) {
                  this.jjmatchedKind = 168;
                  this.jjmatchedPos = 6;
               } else {
                  if ((active3 & 1152921504606846976L) != 0L) {
                     return this.jjStartNfaWithStates_0(6, 252, 300);
                  }

                  if ((active4 & 256L) != 0L) {
                     return this.jjStartNfaWithStates_0(6, 264, 300);
                  }

                  if ((active4 & 262144L) != 0L) {
                     return this.jjStartNfaWithStates_0(6, 274, 300);
                  }

                  if ((active4 & 8388608L) != 0L) {
                     return this.jjStartNfaWithStates_0(6, 279, 300);
                  }

                  if ((active4 & 2251799813685248L) != 0L) {
                     return this.jjStartNfaWithStates_0(6, 307, 300);
                  }

                  if ((active4 & 1152921504606846976L) != 0L) {
                     return this.jjStartNfaWithStates_0(6, 316, 300);
                  }

                  if ((active5 & 16L) != 0L) {
                     return this.jjStartNfaWithStates_0(6, 324, 300);
                  }

                  if ((active5 & 64L) != 0L) {
                     return this.jjStartNfaWithStates_0(6, 326, 300);
                  }

                  if ((active7 & 17179869184L) != 0L) {
                     return this.jjStartNfaWithStates_0(6, 482, 300);
                  }
               }

               return this.jjMoveStringLiteralDfa7_0(active0, 4294967296L, active1, 4612811918351007744L, active2, 2832341972025344L, active3, 2305913386547814400L, active4, 35184372105216L, active5, 2251799813685256L, active6, 4503633987175424L, active7, -9223371487098961912L, active8, 0L);
            }
         case 'U':
         case 'u':
            return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 8589934592L, active5, 0L, active6, 1441151880758558720L, active7, 0L, active8, 0L);
         case 'V':
         case 'v':
            return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 549755813888L, active6, 0L, active7, 0L, active8, 0L);
         case 'W':
         case 'w':
            return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 11540474045136896L, active2, 0L, active3, 0L, active4, 107374182400L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'X':
         case 'x':
            return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0L, active2, 0L, active3, 67108864L, active4, 0L, active5, 0L, active6, 512L, active7, 4611686018427387904L, active8, 1L);
         case 'Y':
         case 'y':
            if ((active5 & 4503599627370496L) != 0L) {
               return this.jjStartNfaWithStates_0(6, 372, 300);
            }

            return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0L, active2, 0L, active3, 268435456L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'Z':
         case 'z':
            return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0L, active2, 67108864L, active3, 0L, active4, 0L, active5, 137438953472L, active6, 0L, active7, 0L, active8, 0L);
         case '_':
            return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 216172782113783808L, active2, 0L, active3, 0L, active4, 8796093022208L, active5, 0L, active6, 2199023255808L, active7, 0L, active8, 67108864L);
         }
      }
   }

   private int jjMoveStringLiteralDfa7_0(long old0, long active0, long old1, long active1, long old2, long active2, long old3, long active3, long old4, long active4, long old5, long active5, long old6, long active6, long old7, long active7, long old8, long active8) {
      if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2) | (active3 &= old3) | (active4 &= old4) | (active5 &= old5) | (active6 &= old6) | (active7 &= old7) | (active8 &= old8)) == 0L) {
         return this.jjStartNfa_0(5, old0, old1, old2, old3, old4, old5, old6, old7, old8, 0L);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var38) {
            this.jjStopStringLiteralDfa_0(6, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
            return 7;
         }

         switch(this.curChar) {
         case ' ':
            return this.jjMoveStringLiteralDfa8_0(active0, 140737488355328L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case '!':
         case '"':
         case '#':
         case '$':
         case '%':
         case '&':
         case '\'':
         case '(':
         case ')':
         case '*':
         case '+':
         case ',':
         case '-':
         case '.':
         case '/':
         case '0':
         case '1':
         case '3':
         case '4':
         case '5':
         case '6':
         case '7':
         case '8':
         case '9':
         case ':':
         case ';':
         case '<':
         case '=':
         case '>':
         case '?':
         case '@':
         case 'J':
         case 'Q':
         case 'X':
         case '[':
         case '\\':
         case ']':
         case '^':
         case '`':
         case 'j':
         case 'q':
         case 'x':
         default:
            break;
         case '2':
            if ((active7 & 274877906944L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 486, 17);
            }
            break;
         case 'A':
         case 'a':
            return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 3458764513820540928L, active2, 8388608L, active3, 1125899907891200L, active4, 1048576L, active5, 137438953472L, active6, 9007199254740993L, active7, 0L, active8, 0L);
         case 'B':
         case 'b':
            if ((active7 & 2251799813685248L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 499, 300);
            }

            if ((active7 & 9007199254740992L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 501, 300);
            }

            return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 281474976710656L, active7, 0L, active8, 0L);
         case 'C':
         case 'c':
            return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 1L, active2, 134217728L, active3, 2199023255552L, active4, 0L, active5, 72057868920029184L, active6, 0L, active7, 0L, active8, 0L);
         case 'D':
         case 'd':
            if ((active2 & 256L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 136, 300);
            }

            if ((active4 & 524288L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 275, 300);
            }

            if ((active4 & 274877906944L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 294, 300);
            }

            if ((active8 & 2L) != 0L) {
               this.jjmatchedKind = 513;
               this.jjmatchedPos = 7;
            }

            return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 216172782113783808L, active2, 0L, active3, 0L, active4, 140737488355328L, active5, 0L, active6, 256L, active7, 0L, active8, 0L);
         case 'E':
         case 'e':
            if ((active0 & 4294967296L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 32, 300);
            }

            if ((active1 & 4L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 66, 300);
            }

            if ((active1 & 16L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 68, 300);
            }

            if ((active1 & 256L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 72, 300);
            }

            if ((active2 & 524288L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 147, 300);
            }

            if ((active2 & 67108864L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 154, 300);
            }

            if ((active2 & 562949953421312L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 177, 300);
            }

            if ((active3 & 8192L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 205, 300);
            }

            if ((active3 & 16777216L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 216, 300);
            }

            if ((active3 & 70368744177664L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 238, 300);
            }

            if ((active3 & 281474976710656L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 240, 300);
            }

            if ((active3 & 562949953421312L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 241, 300);
            }

            if ((active4 & 16384L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 270, 300);
            }

            if ((active4 & 281474976710656L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 304, 300);
            }

            if ((active5 & 549755813888L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 359, 300);
            }

            if ((active6 & 4503599627370496L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 436, 300);
            }

            if ((active6 & 72057594037927936L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 440, 300);
            }

            if ((active6 & 288230376151711744L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 442, 300);
            }

            if ((active6 & 1152921504606846976L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 444, 300);
            }

            if ((active7 & 140737488355328L) != 0L) {
               this.jjmatchedKind = 495;
               this.jjmatchedPos = 7;
            }

            return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 4629700416953647104L, active2, 18874368L, active3, 2L, active4, 17179869184L, active5, 9007199254740992L, active6, 103079477376L, active7, -9223371487098961920L, active8, 402653184L);
         case 'F':
         case 'f':
            return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 0L, active2, 8192L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'G':
         case 'g':
            if ((active4 & 4L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 258, 300);
            }

            if ((active4 & 72057594037927936L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 312, 300);
            }

            return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 4294967296L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 17592186044416L, active7, 0L, active8, 0L);
         case 'H':
         case 'h':
            return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 16L);
         case 'I':
         case 'i':
            return this.jjMoveStringLiteralDfa8_0(active0, 8388608L, active1, 0L, active2, 2260595906707456L, active3, 2305843010287435840L, active4, 70368811319296L, active5, 0L, active6, 67072L, active7, 268L, active8, 0L);
         case 'K':
         case 'k':
            if ((active4 & 128L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 263, 300);
            }

            if ((active5 & 256L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 328, 300);
            }
            break;
         case 'L':
         case 'l':
            if ((active6 & 67108864L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 410, 300);
            }

            if ((active6 & 18014398509481984L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 438, 300);
            }

            if ((active7 & 34359738368L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 483, 300);
            }

            if ((active8 & 16384L) != 0L) {
               this.jjmatchedKind = 526;
               this.jjmatchedPos = 7;
            } else if ((active8 & 16777216L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 536, 300);
            }

            return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 262144L, active2, 4398080070656L, active3, 34359738368L, active4, 1073741824L, active5, 0L, active6, 2882866711604756480L, active7, 4503599627370496L, active8, 67109056L);
         case 'M':
         case 'm':
            if ((active4 & 8589934592L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 289, 300);
            }

            return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 536870912L, active2, 140737488355328L, active3, 0L, active4, 36029346774777856L, active5, 0L, active6, 0L, active7, 0L, active8, 4194560L);
         case 'N':
         case 'n':
            if ((active2 & 1048576L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 148, 300);
            }

            return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 0L, active2, 45071180645793792L, active3, 72057594037927949L, active4, 0L, active5, 4611686018427387904L, active6, 144185582589837312L, active7, 4362076160L, active8, 0L);
         case 'O':
         case 'o':
            return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 576460752303423488L, active2, 1128098930098176L, active3, 20266756668915744L, active4, 35184640524288L, active5, 512L, active6, 0L, active7, 1073741824L, active8, 0L);
         case 'P':
         case 'p':
            return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 562949953421312L, active2, 0L, active3, 268435456L, active4, 0L, active5, 0L, active6, 142936511610880L, active7, 0L, active8, 0L);
         case 'R':
         case 'r':
            if ((active4 & 16777216L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 280, 300);
            }

            if ((active7 & 2199023255552L) != 0L) {
               this.jjmatchedKind = 489;
               this.jjmatchedPos = 7;
            } else if ((active8 & 2048L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 523, 300);
            }

            return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 0L, active2, 4210688L, active3, 0L, active4, 0L, active5, 32768L, active6, 0L, active7, 72061992084439040L, active8, 0L);
         case 'S':
         case 's':
            if ((active0 & 288230376151711744L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 58, 300);
            }

            if ((active1 & 9007199254740992L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 117, 300);
            }

            if ((active1 & 36028797018963968L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 119, 300);
            }

            if ((active2 & 4611686018427387904L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 190, 300);
            }

            if ((active4 & 34359738368L) != 0L) {
               this.jjmatchedKind = 291;
               this.jjmatchedPos = 7;
            } else {
               if ((active4 & 68719476736L) != 0L) {
                  return this.jjStartNfaWithStates_0(7, 292, 300);
               }

               if ((active5 & 8L) != 0L) {
                  return this.jjStartNfaWithStates_0(7, 323, 300);
               }

               if ((active6 & 131072L) != 0L) {
                  this.jjmatchedKind = 401;
                  this.jjmatchedPos = 7;
               } else {
                  if ((active6 & 2097152L) != 0L) {
                     return this.jjStartNfaWithStates_0(7, 405, 300);
                  }

                  if ((active6 & 4194304L) != 0L) {
                     return this.jjStartNfaWithStates_0(7, 406, 300);
                  }
               }
            }

            return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 281474976710656L, active2, 18014398509481984L, active3, 0L, active4, 13194139533312L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'T':
         case 't':
            if ((active0 & 134217728L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 27, 300);
            }

            if ((active2 & 4503599627370496L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 180, 300);
            }

            if ((active3 & 67108864L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 218, 300);
            }

            if ((active4 & 512L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 265, 300);
            }

            if ((active5 & 2L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 321, 300);
            }

            if ((active5 & 8796093022208L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 363, 300);
            }

            if ((active6 & 274877906944L) != 0L) {
               this.jjmatchedKind = 422;
               this.jjmatchedPos = 7;
            } else {
               if ((active7 & 8589934592L) != 0L) {
                  return this.jjStartNfaWithStates_0(7, 481, 300);
               }

               if ((active7 & 4611686018427387904L) != 0L) {
                  return this.jjStartNfaWithStates_0(7, 510, 300);
               }

               if ((active8 & 1L) != 0L) {
                  return this.jjStartNfaWithStates_0(7, 512, 300);
               }
            }

            return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, -9223371967866863616L, active2, 0L, active3, 1024L, active4, 2147491840L, active5, 306244774661193728L, active6, 137438953472L, active7, 281474976711696L, active8, 0L);
         case 'U':
         case 'u':
            return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 288230376151711744L, active2, 17592186044416L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'V':
         case 'v':
            return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 1048576L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'W':
         case 'w':
            if ((active3 & 68719476736L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 228, 300);
            }

            return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 128L, active2, 0L, active3, 0L, active4, 65536L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'Y':
         case 'y':
            if ((active5 & 2251799813685248L) != 0L) {
               this.jjmatchedKind = 371;
               this.jjmatchedPos = 7;
            } else if ((active8 & 1024L) != 0L) {
               return this.jjStartNfaWithStates_0(7, 522, 300);
            }

            return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 1125899906842624L, active2, 0L, active3, 0L, active4, 137438953472L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'Z':
         case 'z':
            return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 0L, active2, 1L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case '_':
            return this.jjMoveStringLiteralDfa8_0(active0, 8589934592L, active1, 2251799813685248L, active2, 0L, active3, 0L, active4, 4831838208L, active5, 0L, active6, 2048L, active7, 0L, active8, 0L);
         }

         return this.jjStartNfa_0(6, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
      }
   }

   private int jjMoveStringLiteralDfa8_0(long old0, long active0, long old1, long active1, long old2, long active2, long old3, long active3, long old4, long active4, long old5, long active5, long old6, long active6, long old7, long active7, long old8, long active8) {
      if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2) | (active3 &= old3) | (active4 &= old4) | (active5 &= old5) | (active6 &= old6) | (active7 &= old7) | (active8 &= old8)) == 0L) {
         return this.jjStartNfa_0(6, old0, old1, old2, old3, old4, old5, old6, old7, old8, 0L);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var38) {
            this.jjStopStringLiteralDfa_0(7, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
            return 8;
         }

         switch(this.curChar) {
         case ' ':
            return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 140737488355328L, active5, 0L, active6, 0L, active7, 0L, active8, 192L);
         case '2':
            if ((active7 & 4398046511104L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 490, 17);
            } else if ((active8 & 134217728L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 539, 17);
            }
         case '!':
         case '"':
         case '#':
         case '$':
         case '%':
         case '&':
         case '\'':
         case '(':
         case ')':
         case '*':
         case '+':
         case ',':
         case '-':
         case '.':
         case '/':
         case '0':
         case '1':
         case '3':
         case '4':
         case '5':
         case '6':
         case '7':
         case '8':
         case '9':
         case ':':
         case ';':
         case '<':
         case '=':
         case '>':
         case '?':
         case '@':
         case 'B':
         case 'F':
         case 'J':
         case 'Q':
         case 'V':
         case 'Z':
         case '[':
         case '\\':
         case ']':
         case '^':
         case '`':
         case 'b':
         case 'f':
         case 'j':
         case 'q':
         case 'v':
         default:
            return this.jjStartNfa_0(7, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
         case 'A':
         case 'a':
            if ((active2 & 140737488355328L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 175, 300);
            }

            return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 72620543991349248L, active2, 35184372088833L, active3, 0L, active4, 549755813888L, active5, 0L, active6, 68719476736L, active7, 16L, active8, 16L);
         case 'C':
         case 'c':
            return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 0L, active2, 8388608L, active3, 0L, active4, 0L, active5, 512L, active6, 66561L, active7, 0L, active8, 0L);
         case 'D':
         case 'd':
            if ((active1 & 16777216L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 88, 300);
            } else if ((active1 & 4611686018427387904L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 126, 300);
            } else if ((active2 & 2097152L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 149, 300);
            } else if ((active3 & 2L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 193, 300);
            } else if ((active5 & 9007199254740992L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 373, 300);
            } else {
               if ((active6 & 34359738368L) != 0L) {
                  return this.jjStartNfaWithStates_0(8, 419, 300);
               }

               return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 0L, active2, 16384L, active3, 0L, active4, 0L, active5, 0L, active6, 70368744177664L, active7, 0L, active8, 0L);
            }
         case 'E':
         case 'e':
            if ((active1 & 1048576L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 84, 300);
            } else if ((active2 & 1024L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 138, 300);
            } else if ((active2 & 4096L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 140, 300);
            } else if ((active3 & 268435456L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 220, 300);
            } else if ((active4 & 8192L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 269, 300);
            } else {
               if ((active7 & 1024L) != 0L) {
                  return this.jjStartNfaWithStates_0(8, 458, 300);
               }

               return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 73014444032L, active2, 0L, active3, 1088L, active4, 4398046511104L, active5, 90071992547409920L, active6, 158329808617472L, active7, 281475043819520L, active8, 256L);
            }
         case 'G':
         case 'g':
            if ((active2 & 9007199254740992L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 181, 300);
            } else if ((active2 & 36028797018963968L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 183, 300);
            } else if ((active3 & 4L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 194, 300);
            } else if ((active3 & 8L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 195, 300);
            } else if ((active3 & 72057594037927936L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 248, 300);
            } else {
               if ((active5 & 4611686018427387904L) != 0L) {
                  return this.jjStartNfaWithStates_0(8, 382, 300);
               }

               return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 1152921504606846976L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
         case 'H':
         case 'h':
            return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 0L, active2, 18014398643699712L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'I':
         case 'i':
            return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 805568512L, active2, 8192L, active3, 0L, active4, 65536L, active5, 0L, active6, 0L, active7, 0L, active8, 67108864L);
         case 'K':
         case 'k':
            return this.jjMoveStringLiteralDfa9_0(active0, 8589934592L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'L':
         case 'l':
            return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 0L, active2, 33554432L, active3, 0L, active4, 5368709120L, active5, 0L, active6, 281474976710656L, active7, 0L, active8, 0L);
         case 'M':
         case 'm':
            return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 32768L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'N':
         case 'n':
            if ((active2 & 2199023255552L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 169, 300);
            } else if ((active3 & 32L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 197, 300);
            } else {
               if ((active3 & 18014398509481984L) != 0L) {
                  this.jjmatchedKind = 246;
                  this.jjmatchedPos = 8;
               } else if ((active7 & 1073741824L) != 0L) {
                  return this.jjStartNfaWithStates_0(8, 478, 300);
               }

               return this.jjMoveStringLiteralDfa9_0(active0, 8388608L, active1, 0L, active2, 1125899906842624L, active3, 2252350643240960L, active4, 70368745226240L, active5, 0L, active6, 0L, active7, 260L, active8, 0L);
            }
         case 'O':
         case 'o':
            return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 0L, active2, 8796093022208L, active3, 2233382993920L, active4, 67108864L, active5, 288230376151711744L, active6, 2199023255552L, active7, 4503599627370504L, active8, 268435456L);
         case 'P':
         case 'p':
            if ((active4 & 36028797018963968L) != 0L) {
               this.jjmatchedKind = 311;
               this.jjmatchedPos = 8;
            }

            return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 288230376151711744L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 4194304L);
         case 'R':
         case 'r':
            if ((active4 & 35184372088832L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 301, 300);
            } else {
               if ((active7 & 549755813888L) != 0L) {
                  return this.jjStartNfaWithStates_0(8, 487, 300);
               }

               return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 0L, active2, 16777216L, active3, 8589934592L, active4, 805306368L, active5, 274877906944L, active6, 0L, active7, 0L, active8, 0L);
            }
         case 'S':
         case 's':
            if ((active1 & 18014398509481984L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 118, 300);
            } else if ((active2 & 4398046511104L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 170, 300);
            } else if ((active4 & 137438953472L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 293, 300);
            } else {
               if ((active6 & 137438953472L) != 0L) {
                  return this.jjStartNfaWithStates_0(8, 421, 300);
               }

               return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 2251799813685248L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 262656L, active7, 0L, active8, 0L);
            }
         case 'T':
         case 't':
            if ((active3 & 1L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 192, 300);
            } else if ((active5 & 4194304L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 342, 300);
            } else if ((active6 & 144115188075855872L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 441, 300);
            } else {
               if ((active7 & 4294967296L) != 0L) {
                  return this.jjStartNfaWithStates_0(8, 480, 300);
               }

               return this.jjMoveStringLiteralDfa9_0(active0, 140737488355328L, active1, -6917529027641081728L, active2, 2269391999729664L, active3, 2306968909121585152L, active4, 8796093022208L, active5, 137438953472L, active6, 9007225024546816L, active7, 0L, active8, 0L);
            }
         case 'U':
         case 'u':
            return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 144115188075855872L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 2882303761517117696L, active7, 0L, active8, 0L);
         case 'W':
         case 'w':
            return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 576460752303423488L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'X':
         case 'x':
            if ((active6 & 128L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 391, 300);
            }

            return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, Long.MIN_VALUE, active8, 0L);
         case 'Y':
         case 'y':
            if ((active2 & 4194304L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 150, 300);
            } else if ((active5 & 32768L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 335, 300);
            } else if ((active6 & 562949953421312L) != 0L) {
               return this.jjStartNfaWithStates_0(8, 433, 300);
            } else {
               if ((active7 & 72057594037927936L) != 0L) {
                  return this.jjStartNfaWithStates_0(8, 504, 300);
               }

               return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 17179869184L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
         case '_':
            return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 1407374883553281L, active2, 0L, active3, 0L, active4, 2147483648L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         }
      }
   }

   private int jjMoveStringLiteralDfa9_0(long old0, long active0, long old1, long active1, long old2, long active2, long old3, long active3, long old4, long active4, long old5, long active5, long old6, long active6, long old7, long active7, long old8, long active8) {
      if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2) | (active3 &= old3) | (active4 &= old4) | (active5 &= old5) | (active6 &= old6) | (active7 &= old7) | (active8 &= old8)) == 0L) {
         return this.jjStartNfa_0(7, old0, old1, old2, old3, old4, old5, old6, old7, old8, 0L);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var38) {
            this.jjStopStringLiteralDfa_0(8, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
            return 9;
         }

         switch(this.curChar) {
         case 'A':
         case 'a':
            return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 0L, active2, 17592186044416L, active3, 0L, active4, 8796093054976L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'B':
         case 'b':
            if ((active7 & 4503599627370496L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 500, 300);
            }

            return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 0L, active2, 1L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'C':
         case 'M':
         case 'Q':
         case 'U':
         case 'V':
         case 'X':
         case '[':
         case '\\':
         case ']':
         case '^':
         case '`':
         case 'c':
         case 'm':
         case 'q':
         case 'u':
         case 'v':
         case 'x':
         default:
            break;
         case 'D':
         case 'd':
            if ((active1 & 68719476736L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 100, 300);
            }

            if ((active3 & 64L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 198, 300);
            }

            if ((active4 & 65536L) != 0L) {
               return this.jjStopAtPos(9, 272);
            }

            if ((active4 & 4398046511104L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 298, 300);
            }

            return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 20L, active8, 128L);
         case 'E':
         case 'e':
            if ((active2 & 8388608L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 151, 300);
            }

            if ((active2 & 134217728L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 155, 300);
            }

            if ((active3 & 1048576L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 212, 300);
            }

            if ((active3 & 1125899906842624L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 242, 300);
            }

            if ((active6 & 1L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 384, 300);
            }

            if ((active6 & 281474976710656L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 432, 300);
            }

            if ((active6 & 9007199254740992L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 437, 300);
            }

            if ((active6 & 576460752303423488L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 443, 300);
            }

            if ((active6 & 2305843009213693952L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 445, 300);
            }

            return this.jjMoveStringLiteralDfa10_0(active0, 8589934592L, active1, -8070450532247928832L, active2, 0L, active3, 0L, active4, 4831838208L, active5, 274877906944L, active6, 70368744179712L, active7, 0L, active8, 0L);
         case 'F':
         case 'f':
            return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 1L, active2, 16384L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 268435456L);
         case 'G':
         case 'g':
            if ((active3 & 1073741824L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 222, 300);
            }

            return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 1688849860263936L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'H':
         case 'h':
            if ((active1 & 128L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 71, 300);
            }
            break;
         case 'I':
         case 'i':
            return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 2308094809027379200L, active2, 2251799813685248L, active3, 2305843567559442432L, active4, 140737756790784L, active5, 137438953472L, active6, 0L, active7, 0L, active8, 0L);
         case 'J':
         case 'j':
            return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 2147483648L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'K':
         case 'k':
            if ((active4 & 1048576L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 276, 300);
            }

            if ((active5 & 512L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 329, 300);
            }

            return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 67108864L);
         case 'L':
         case 'l':
            if ((active2 & 35184372088832L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 173, 300);
            }

            if ((active3 & 2199023255552L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 233, 300);
            }

            if ((active6 & 134217728L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 411, 300);
            }

            return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 0L, active2, 8192L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'N':
         case 'n':
            if ((active2 & 8796093022208L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 171, 300);
            }

            if ((active7 & 8L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 451, 300);
            }

            return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 140737488355328L, active7, 0L, active8, 0L);
         case 'O':
         case 'o':
            if ((active0 & 140737488355328L) != 0L) {
               return this.jjStopAtPos(9, 47);
            }

            return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 268435456L, active2, 18014398509481984L, active3, 0L, active4, 0L, active5, 0L, active6, 2199023255552L, active7, 0L, active8, 0L);
         case 'P':
         case 'p':
            return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 144396663052566528L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 256L, active7, 0L, active8, 0L);
         case 'R':
         case 'r':
            if ((active3 & 1024L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 202, 300);
            }

            if ((active5 & 288230376151711744L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 378, 300);
            }

            if ((active8 & 16L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 516, 300);
            }

            return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 67108864L, active5, 18014398509481984L, active6, 0L, active7, 0L, active8, 0L);
         case 'S':
         case 's':
            if ((active1 & 4294967296L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 96, 300);
            }

            if ((active2 & 16777216L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 152, 300);
            }

            if ((active2 & 1125899906842624L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 178, 300);
            }

            if ((active3 & 2251799813685248L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 243, 300);
            }

            if ((active5 & 72057594037927936L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 376, 300);
            }

            if ((active6 & 65536L) != 0L) {
               this.jjmatchedKind = 400;
               this.jjmatchedPos = 9;
            } else {
               if ((active6 & 262144L) != 0L) {
                  return this.jjStartNfaWithStates_0(9, 402, 300);
               }

               if ((active6 & 8589934592L) != 0L) {
                  return this.jjStartNfaWithStates_0(9, 417, 300);
               }

               if ((active6 & 17179869184L) != 0L) {
                  return this.jjStartNfaWithStates_0(9, 418, 300);
               }
            }

            return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 68719477760L, active7, 0L, active8, 0L);
         case 'T':
         case 't':
            if ((active0 & 8388608L) != 0L) {
               this.jjmatchedKind = 23;
               this.jjmatchedPos = 9;
            } else {
               if ((active1 & 536870912L) != 0L) {
                  return this.jjStartNfaWithStates_0(9, 93, 300);
               }

               if ((active4 & 549755813888L) != 0L) {
                  return this.jjStartNfaWithStates_0(9, 295, 300);
               }

               if ((active7 & Long.MIN_VALUE) != 0L) {
                  return this.jjStartNfaWithStates_0(9, 511, 300);
               }
            }

            return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 72057594037927936L, active2, 0L, active3, 0L, active4, 70368744177664L, active5, 0L, active6, 512L, active7, 281474976710912L, active8, 4194560L);
         case 'W':
         case 'w':
            if ((active3 & 34359738368L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 227, 300);
            }
            break;
         case 'Y':
         case 'y':
            if ((active2 & 33554432L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 153, 300);
            }

            if ((active7 & 67108864L) != 0L) {
               return this.jjStartNfaWithStates_0(9, 474, 300);
            }

            return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 64L);
         case 'Z':
         case 'z':
            return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 262144L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case '_':
            return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 864691128455135232L, active2, 0L, active3, 0L, active4, 18253611008L, active5, 0L, active6, 17592186044416L, active7, 0L, active8, 0L);
         }

         return this.jjStartNfa_0(8, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
      }
   }

   private int jjMoveStringLiteralDfa10_0(long old0, long active0, long old1, long active1, long old2, long active2, long old3, long active3, long old4, long active4, long old5, long active5, long old6, long active6, long old7, long active7, long old8, long active8) {
      if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2) | (active3 &= old3) | (active4 &= old4) | (active5 &= old5) | (active6 &= old6) | (active7 &= old7) | (active8 &= old8)) == 0L) {
         return this.jjStartNfa_0(8, old0, old1, old2, old3, old4, old5, old6, old7, old8, 0L);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var38) {
            this.jjStopStringLiteralDfa_0(9, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
            return 10;
         }

         switch(this.curChar) {
         case 'A':
         case 'a':
            if ((active1 & 72057594037927936L) != 0L) {
               return this.jjStartNfaWithStates_0(10, 120, 300);
            }

            return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 1125899906842624L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 16L, active8, 128L);
         case 'B':
         case 'b':
            return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 0L, active2, 17592186044416L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'C':
         case 'G':
         case 'H':
         case 'J':
         case 'K':
         case 'P':
         case 'Q':
         case 'U':
         case 'V':
         case 'X':
         case '[':
         case '\\':
         case ']':
         case '^':
         case '`':
         case 'c':
         case 'g':
         case 'h':
         case 'j':
         case 'k':
         case 'p':
         case 'q':
         case 'u':
         case 'v':
         case 'x':
         default:
            return this.jjStartNfa_0(9, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
         case 'D':
         case 'd':
            if ((active1 & Long.MIN_VALUE) != 0L) {
               return this.jjStartNfaWithStates_0(10, 127, 300);
            }

            return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 140737488355328L, active7, 0L, active8, 0L);
         case 'E':
         case 'e':
            if ((active2 & 8192L) != 0L) {
               return this.jjStartNfaWithStates_0(10, 141, 300);
            } else if ((active6 & 68719476736L) != 0L) {
               return this.jjStartNfaWithStates_0(10, 420, 300);
            } else {
               if ((active8 & 67108864L) != 0L) {
                  return this.jjStartNfaWithStates_0(10, 538, 300);
               }

               return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 844424930394112L, active2, 0L, active3, 0L, active4, 70368744177664L, active5, 18014398509481984L, active6, 0L, active7, 0L, active8, 64L);
            }
         case 'F':
         case 'f':
            return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 268435456L);
         case 'I':
         case 'i':
            return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 0L, active2, 16384L, active3, 0L, active4, 67108864L, active5, 0L, active6, 512L, active7, 281474976710660L, active8, 0L);
         case 'L':
         case 'l':
            if ((active6 & 2199023255552L) != 0L) {
               return this.jjStartNfaWithStates_0(10, 425, 300);
            }

            return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 576460752303423488L, active2, 18014398509481985L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'M':
         case 'm':
            return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 274877906944L, active6, 2048L, active7, 0L, active8, 0L);
         case 'N':
         case 'n':
            if ((active1 & 268435456L) != 0L) {
               return this.jjStartNfaWithStates_0(10, 92, 300);
            }

            return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 0L, active2, 0L, active3, 558345748480L, active4, 140741783322624L, active5, 0L, active6, 70368744177664L, active7, 0L, active8, 0L);
         case 'O':
         case 'o':
            return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 2305843009213693953L, active2, 2251799813685248L, active3, 2305843009213693952L, active4, 2147483648L, active5, 137438953472L, active6, 17592186044416L, active7, 0L, active8, 0L);
         case 'R':
         case 'r':
            return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 288230376151711744L, active2, 0L, active3, 0L, active4, 1073774592L, active5, 0L, active6, 0L, active7, 0L, active8, 256L);
         case 'S':
         case 's':
            if ((active7 & 256L) != 0L) {
               return this.jjStartNfaWithStates_0(10, 456, 300);
            }

            return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 536870912L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'T':
         case 't':
            return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 8796361457664L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'W':
         case 'w':
            return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 17179869184L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'Y':
         case 'y':
            return this.jjMoveStringLiteralDfa11_0(active0, 8589934592L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'Z':
         case 'z':
            if ((active8 & 4194304L) != 0L) {
               return this.jjStartNfaWithStates_0(10, 534, 300);
            }

            return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 2251799813685248L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case '_':
            return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 1297036692682702848L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 1280L, active7, 0L, active8, 0L);
         }
      }
   }

   private int jjMoveStringLiteralDfa11_0(long old0, long active0, long old1, long active1, long old2, long active2, long old3, long active3, long old4, long active4, long old5, long active5, long old6, long active6, long old7, long active7, long old8, long active8) {
      if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2) | (active3 &= old3) | (active4 &= old4) | (active5 &= old5) | (active6 &= old6) | (active7 &= old7) | (active8 &= old8)) == 0L) {
         return this.jjStartNfa_0(9, old0, old1, old2, old3, old4, old5, old6, old7, old8, 0L);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var38) {
            this.jjStopStringLiteralDfa_0(10, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
            return 11;
         }

         switch(this.curChar) {
         case 'A':
         case 'a':
            return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 64L);
         case 'C':
         case 'c':
            return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 70368744177664L, active7, 0L, active8, 0L);
         case 'D':
         case 'd':
            if ((active1 & 262144L) != 0L) {
               return this.jjStartNfaWithStates_0(11, 82, 300);
            } else if ((active2 & 18014398509481984L) != 0L) {
               return this.jjStartNfaWithStates_0(11, 182, 300);
            } else if ((active5 & 18014398509481984L) != 0L) {
               return this.jjStartNfaWithStates_0(11, 374, 300);
            }
         case 'B':
         case 'F':
         case 'H':
         case 'J':
         case 'Q':
         case 'V':
         case 'W':
         case 'X':
         case 'Z':
         case '[':
         case '\\':
         case ']':
         case '^':
         case '`':
         case 'b':
         case 'f':
         case 'h':
         case 'j':
         case 'q':
         case 'v':
         case 'w':
         case 'x':
         default:
            return this.jjStartNfa_0(10, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
         case 'E':
         case 'e':
            if ((active1 & 2251799813685248L) != 0L) {
               return this.jjStartNfaWithStates_0(11, 115, 300);
            } else {
               if ((active2 & 1L) != 0L) {
                  return this.jjStartNfaWithStates_0(11, 128, 300);
               }

               return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 8797166764032L, active5, 274877906944L, active6, 140737488355328L, active7, 0L, active8, 0L);
            }
         case 'G':
         case 'g':
            if ((active3 & 8589934592L) != 0L) {
               return this.jjStartNfaWithStates_0(11, 225, 300);
            } else {
               if ((active3 & 549755813888L) != 0L) {
                  return this.jjStartNfaWithStates_0(11, 231, 300);
               }

               return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 562949953421312L, active2, 0L, active3, 0L, active4, 70373039144960L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
         case 'I':
         case 'i':
            return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 2147483648L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'K':
         case 'k':
            return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 256L, active7, 0L, active8, 0L);
         case 'L':
         case 'l':
            return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 1152921504606846976L, active2, 17592186060800L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'M':
         case 'm':
            return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 281474976710656L, active8, 0L);
         case 'N':
         case 'n':
            if ((active1 & 2305843009213693952L) != 0L) {
               return this.jjStopAtPos(11, 125);
            } else {
               if ((active3 & 2305843009213693952L) != 0L) {
                  this.jjmatchedKind = 253;
                  this.jjmatchedPos = 11;
               } else {
                  if ((active5 & 137438953472L) != 0L) {
                     return this.jjStartNfaWithStates_0(11, 357, 300);
                  }

                  if ((active6 & 17592186044416L) != 0L) {
                     return this.jjStartNfaWithStates_0(11, 428, 300);
                  }
               }

               return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 0L, active2, 2251799813685248L, active3, 0L, active4, 0L, active5, 0L, active6, 1536L, active7, 4L, active8, 0L);
            }
         case 'O':
         case 'o':
            return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 864691128455135232L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'P':
         case 'p':
            if ((active1 & 1125899906842624L) != 0L) {
               return this.jjStartNfaWithStates_0(11, 114, 300);
            }

            return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 2048L, active7, 0L, active8, 0L);
         case 'R':
         case 'r':
            return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 144396663052566528L, active2, 0L, active3, 0L, active4, 17179869184L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'S':
         case 's':
            return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 268435456L);
         case 'T':
         case 't':
            return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 140737555464192L, active5, 0L, active6, 0L, active7, 16L, active8, 0L);
         case 'U':
         case 'u':
            return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 1L, active2, 0L, active3, 0L, active4, 536870912L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'Y':
         case 'y':
            if ((active4 & 268435456L) != 0L) {
               return this.jjStartNfaWithStates_0(11, 284, 300);
            } else {
               if ((active8 & 256L) != 0L) {
                  return this.jjStartNfaWithStates_0(11, 520, 300);
               }

               return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 32768L, active5, 0L, active6, 0L, active7, 0L, active8, 128L);
            }
         case '_':
            return this.jjMoveStringLiteralDfa12_0(active0, 8589934592L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         }
      }
   }

   private int jjMoveStringLiteralDfa12_0(long old0, long active0, long old1, long active1, long old2, long active2, long old3, long active3, long old4, long active4, long old5, long active5, long old6, long active6, long old7, long active7, long old8, long active8) {
      if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2) | active3 & old3 | (active4 &= old4) | (active5 &= old5) | (active6 &= old6) | (active7 &= old7) | (active8 &= old8)) == 0L) {
         return this.jjStartNfa_0(10, old0, old1, old2, old3, old4, old5, old6, old7, old8, 0L);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var38) {
            this.jjStopStringLiteralDfa_0(11, active0, active1, active2, 0L, active4, active5, active6, active7, active8, 0L);
            return 12;
         }

         switch(this.curChar) {
         case ' ':
            return this.jjMoveStringLiteralDfa13_0(active0, 0L, active1, 0L, active2, 0L, active4, 32768L, active5, 0L, active6, 0L, active7, 0L, active8, 128L);
         case '!':
         case '"':
         case '#':
         case '$':
         case '%':
         case '&':
         case '\'':
         case '(':
         case ')':
         case '*':
         case '+':
         case ',':
         case '-':
         case '.':
         case '/':
         case '0':
         case '1':
         case '2':
         case '3':
         case '4':
         case '5':
         case '6':
         case '7':
         case '8':
         case '9':
         case ':':
         case ';':
         case '<':
         case '=':
         case '>':
         case '?':
         case '@':
         case 'B':
         case 'F':
         case 'H':
         case 'J':
         case 'K':
         case 'P':
         case 'Q':
         case 'U':
         case 'V':
         case 'X':
         case 'Z':
         case '[':
         case '\\':
         case ']':
         case '^':
         case '`':
         case 'b':
         case 'f':
         case 'h':
         case 'j':
         case 'k':
         case 'p':
         case 'q':
         case 'u':
         case 'v':
         case 'x':
         default:
            break;
         case 'A':
         case 'a':
            if ((active7 & 16L) != 0L) {
               return this.jjStartNfaWithStates_0(12, 452, 300);
            }

            return this.jjMoveStringLiteralDfa13_0(active0, 0L, active1, 562949953421312L, active2, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'C':
         case 'c':
            return this.jjMoveStringLiteralDfa13_0(active0, 8589934592L, active1, 576460752303423488L, active2, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'D':
         case 'd':
            return this.jjMoveStringLiteralDfa13_0(active0, 0L, active1, 0L, active2, 0L, active4, 0L, active5, 0L, active6, 2048L, active7, 0L, active8, 0L);
         case 'E':
         case 'e':
            if ((active2 & 16384L) != 0L) {
               return this.jjStartNfaWithStates_0(12, 142, 300);
            }

            if ((active2 & 17592186044416L) != 0L) {
               return this.jjStartNfaWithStates_0(12, 172, 300);
            }

            if ((active7 & 281474976710656L) != 0L) {
               return this.jjStartNfaWithStates_0(12, 496, 300);
            }

            return this.jjMoveStringLiteralDfa13_0(active0, 0L, active1, 0L, active2, 0L, active4, 211106232532992L, active5, 0L, active6, 256L, active7, 0L, active8, 268435456L);
         case 'G':
         case 'g':
            if ((active6 & 512L) != 0L) {
               return this.jjStartNfaWithStates_0(12, 393, 300);
            }

            if ((active7 & 4L) != 0L) {
               return this.jjStartNfaWithStates_0(12, 450, 300);
            }
            break;
         case 'I':
         case 'i':
            return this.jjMoveStringLiteralDfa13_0(active0, 0L, active1, 0L, active2, 0L, active4, 17179869184L, active5, 0L, active6, 70368744177664L, active7, 0L, active8, 0L);
         case 'L':
         case 'l':
            return this.jjMoveStringLiteralDfa13_0(active0, 0L, active1, 0L, active2, 0L, active4, 536870912L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'M':
         case 'm':
            return this.jjMoveStringLiteralDfa13_0(active0, 0L, active1, 0L, active2, 0L, active4, 8796093022208L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'N':
         case 'n':
            if ((active4 & 2147483648L) != 0L) {
               return this.jjStartNfaWithStates_0(12, 287, 300);
            }

            return this.jjMoveStringLiteralDfa13_0(active0, 0L, active1, 1L, active2, 0L, active4, 0L, active5, 274877906944L, active6, 140737488355328L, active7, 0L, active8, 0L);
         case 'O':
         case 'o':
            return this.jjMoveStringLiteralDfa13_0(active0, 0L, active1, 1297036692682702848L, active2, 0L, active4, 0L, active5, 0L, active6, 1024L, active7, 0L, active8, 0L);
         case 'R':
         case 'r':
            return this.jjMoveStringLiteralDfa13_0(active0, 0L, active1, 0L, active2, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 64L);
         case 'S':
         case 's':
            if ((active2 & 2251799813685248L) != 0L) {
               return this.jjStartNfaWithStates_0(12, 179, 300);
            }

            return this.jjMoveStringLiteralDfa13_0(active0, 0L, active1, 0L, active2, 0L, active4, 1073741824L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'T':
         case 't':
            return this.jjMoveStringLiteralDfa13_0(active0, 0L, active1, 0L, active2, 0L, active4, 4294967296L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         case 'W':
         case 'w':
            if ((active1 & 288230376151711744L) != 0L) {
               return this.jjStartNfaWithStates_0(12, 122, 300);
            }
            break;
         case 'Y':
         case 'y':
            if ((active4 & 67108864L) != 0L) {
               return this.jjStartNfaWithStates_0(12, 282, 300);
            }
            break;
         case '_':
            return this.jjMoveStringLiteralDfa13_0(active0, 0L, active1, 281474976710656L, active2, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
         }

         return this.jjStartNfa_0(11, active0, active1, active2, 0L, active4, active5, active6, active7, active8, 0L);
      }
   }

   private int jjMoveStringLiteralDfa13_0(long old0, long active0, long old1, long active1, long old2, long active2, long old4, long active4, long old5, long active5, long old6, long active6, long old7, long active7, long old8, long active8) {
      if (((active0 &= old0) | (active1 &= old1) | active2 & old2 | (active4 &= old4) | (active5 &= old5) | (active6 &= old6) | active7 & old7 | (active8 &= old8)) == 0L) {
         return this.jjStartNfa_0(11, old0, old1, old2, 0L, old4, old5, old6, old7, old8, 0L);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var34) {
            this.jjStopStringLiteralDfa_0(12, active0, active1, 0L, 0L, active4, active5, active6, 0L, active8, 0L);
            return 13;
         }

         switch(this.curChar) {
         case ' ':
            return this.jjMoveStringLiteralDfa14_0(active0, 0L, active1, 0L, active4, 0L, active5, 0L, active6, 0L, active8, 64L);
         case '!':
         case '"':
         case '#':
         case '$':
         case '%':
         case '&':
         case '\'':
         case '(':
         case ')':
         case '*':
         case '+':
         case ',':
         case '-':
         case '.':
         case '/':
         case '0':
         case '1':
         case '2':
         case '3':
         case '4':
         case '5':
         case '6':
         case '7':
         case '8':
         case '9':
         case ':':
         case ';':
         case '<':
         case '=':
         case '>':
         case '?':
         case '@':
         case 'A':
         case 'F':
         case 'I':
         case 'J':
         case 'L':
         case 'M':
         case 'N':
         case 'O':
         case 'Q':
         case 'S':
         case 'V':
         case 'X':
         case 'Z':
         case '[':
         case '\\':
         case ']':
         case '^':
         case '_':
         case '`':
         case 'a':
         case 'f':
         case 'i':
         case 'j':
         case 'l':
         case 'm':
         case 'n':
         case 'o':
         case 'q':
         case 's':
         case 'v':
         case 'x':
         default:
            break;
         case 'B':
         case 'b':
            if ((active6 & 2048L) != 0L) {
               return this.jjStartNfaWithStates_0(13, 395, 300);
            }
            break;
         case 'C':
         case 'c':
            return this.jjMoveStringLiteralDfa14_0(active0, 0L, active1, 1152921504606846976L, active4, 0L, active5, 0L, active6, 140737488355328L, active8, 0L);
         case 'D':
         case 'd':
            return this.jjMoveStringLiteralDfa14_0(active0, 0L, active1, 1L, active4, 0L, active5, 0L, active6, 0L, active8, 0L);
         case 'E':
         case 'e':
            return this.jjMoveStringLiteralDfa14_0(active0, 0L, active1, 0L, active4, 8796093022208L, active5, 0L, active6, 70368744177664L, active8, 0L);
         case 'G':
         case 'g':
            return this.jjMoveStringLiteralDfa14_0(active0, 0L, active1, 0L, active4, 140737488355328L, active5, 0L, active6, 0L, active8, 0L);
         case 'H':
         case 'h':
            if ((active4 & 4294967296L) != 0L) {
               return this.jjStartNfaWithStates_0(13, 288, 300);
            }

            return this.jjMoveStringLiteralDfa14_0(active0, 8589934592L, active1, 0L, active4, 0L, active5, 0L, active6, 0L, active8, 0L);
         case 'K':
         case 'k':
            return this.jjMoveStringLiteralDfa14_0(active0, 0L, active1, 576460752303423488L, active4, 32768L, active5, 0L, active6, 0L, active8, 0L);
         case 'P':
         case 'p':
            if ((active1 & 562949953421312L) != 0L) {
               return this.jjStartNfaWithStates_0(13, 113, 300);
            }

            return this.jjMoveStringLiteralDfa14_0(active0, 0L, active1, 281474976710656L, active4, 0L, active5, 0L, active6, 0L, active8, 0L);
         case 'R':
         case 'r':
            if ((active4 & 70368744177664L) != 0L) {
               return this.jjStopAtPos(13, 302);
            }

            return this.jjMoveStringLiteralDfa14_0(active0, 0L, active1, 0L, active4, 0L, active5, 0L, active6, 1024L, active8, 0L);
         case 'T':
         case 't':
            if ((active4 & 536870912L) != 0L) {
               return this.jjStartNfaWithStates_0(13, 285, 300);
            }

            if ((active5 & 274877906944L) != 0L) {
               return this.jjStartNfaWithStates_0(13, 358, 300);
            }

            if ((active8 & 268435456L) != 0L) {
               return this.jjStartNfaWithStates_0(13, 540, 300);
            }

            return this.jjMoveStringLiteralDfa14_0(active0, 0L, active1, 0L, active4, 17179869184L, active5, 0L, active6, 0L, active8, 128L);
         case 'U':
         case 'u':
            return this.jjMoveStringLiteralDfa14_0(active0, 0L, active1, 0L, active4, 1073741824L, active5, 0L, active6, 0L, active8, 0L);
         case 'W':
         case 'w':
            if ((active1 & 144115188075855872L) != 0L) {
               return this.jjStartNfaWithStates_0(13, 121, 300);
            }
            break;
         case 'Y':
         case 'y':
            if ((active6 & 256L) != 0L) {
               return this.jjStartNfaWithStates_0(13, 392, 300);
            }
         }

         return this.jjStartNfa_0(12, active0, active1, 0L, 0L, active4, active5, active6, 0L, active8, 0L);
      }
   }

   private int jjMoveStringLiteralDfa14_0(long old0, long active0, long old1, long active1, long old4, long active4, long old5, long active5, long old6, long active6, long old8, long active8) {
      if (((active0 &= old0) | (active1 &= old1) | (active4 &= old4) | active5 & old5 | (active6 &= old6) | (active8 &= old8)) == 0L) {
         return this.jjStartNfa_0(12, old0, old1, 0L, 0L, old4, old5, old6, 0L, old8, 0L);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var26) {
            this.jjStopStringLiteralDfa_0(13, active0, active1, 0L, 0L, active4, 0L, active6, 0L, active8, 0L);
            return 14;
         }

         switch(this.curChar) {
         case 'A':
         case 'a':
            return this.jjMoveStringLiteralDfa15_0(active0, 0L, active1, 281474976710656L, active4, 0L, active6, 0L, active8, 0L);
         case 'E':
         case 'e':
            if ((active4 & 17179869184L) != 0L) {
               return this.jjStartNfaWithStates_0(14, 290, 300);
            }

            return this.jjMoveStringLiteralDfa15_0(active0, 8589934592L, active1, 0L, active4, 140737488388096L, active6, 1024L, active8, 0L);
         case 'I':
         case 'i':
            return this.jjMoveStringLiteralDfa15_0(active0, 0L, active1, 0L, active4, 0L, active6, 140737488355328L, active8, 0L);
         case 'K':
         case 'k':
            return this.jjMoveStringLiteralDfa15_0(active0, 0L, active1, 1152921504606846976L, active4, 0L, active6, 0L, active8, 0L);
         case 'L':
         case 'l':
            return this.jjMoveStringLiteralDfa15_0(active0, 0L, active1, 0L, active4, 1073741824L, active6, 0L, active8, 0L);
         case 'N':
         case 'n':
            return this.jjMoveStringLiteralDfa15_0(active0, 0L, active1, 0L, active4, 8796093022208L, active6, 0L, active8, 0L);
         case 'O':
         case 'o':
            return this.jjMoveStringLiteralDfa15_0(active0, 0L, active1, 0L, active4, 0L, active6, 0L, active8, 128L);
         case 'S':
         case 's':
            if ((active1 & 576460752303423488L) != 0L) {
               return this.jjStartNfaWithStates_0(14, 123, 300);
            } else if ((active6 & 70368744177664L) != 0L) {
               return this.jjStartNfaWithStates_0(14, 430, 300);
            }
         case 'B':
         case 'C':
         case 'D':
         case 'F':
         case 'G':
         case 'H':
         case 'J':
         case 'M':
         case 'P':
         case 'Q':
         case 'R':
         case 'U':
         case 'V':
         case 'W':
         case 'X':
         case 'Y':
         case 'Z':
         case '[':
         case '\\':
         case ']':
         case '^':
         case '`':
         case 'b':
         case 'c':
         case 'd':
         case 'f':
         case 'g':
         case 'h':
         case 'j':
         case 'm':
         case 'p':
         case 'q':
         case 'r':
         default:
            return this.jjStartNfa_0(13, active0, active1, 0L, 0L, active4, 0L, active6, 0L, active8, 0L);
         case 'T':
         case 't':
            return this.jjMoveStringLiteralDfa15_0(active0, 0L, active1, 0L, active4, 0L, active6, 0L, active8, 64L);
         case '_':
            return this.jjMoveStringLiteralDfa15_0(active0, 0L, active1, 1L, active4, 0L, active6, 0L, active8, 0L);
         }
      }
   }

   private int jjMoveStringLiteralDfa15_0(long old0, long active0, long old1, long active1, long old4, long active4, long old6, long active6, long old8, long active8) {
      if (((active0 &= old0) | (active1 &= old1) | (active4 &= old4) | (active6 &= old6) | (active8 &= old8)) == 0L) {
         return this.jjStartNfa_0(13, old0, old1, 0L, 0L, old4, 0L, old6, 0L, old8, 0L);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var22) {
            this.jjStopStringLiteralDfa_0(14, active0, active1, 0L, 0L, active4, 0L, active6, 0L, active8, 0L);
            return 15;
         }

         switch(this.curChar) {
         case ' ':
            return this.jjMoveStringLiteralDfa16_0(active0, 0L, active1, 0L, active4, 0L, active6, 0L, active8, 128L);
         case 'C':
         case 'c':
            return this.jjMoveStringLiteralDfa16_0(active0, 8589934592L, active1, 0L, active4, 0L, active6, 1024L, active8, 0L);
         case 'E':
         case 'e':
            return this.jjMoveStringLiteralDfa16_0(active0, 0L, active1, 0L, active4, 0L, active6, 140737488355328L, active8, 0L);
         case 'G':
         case 'g':
            return this.jjMoveStringLiteralDfa16_0(active0, 0L, active1, 281474976710656L, active4, 0L, active6, 0L, active8, 0L);
         case 'O':
         case 'o':
            return this.jjMoveStringLiteralDfa16_0(active0, 0L, active1, 0L, active4, 0L, active6, 0L, active8, 64L);
         case 'R':
         case 'r':
            if ((active4 & 140737488355328L) != 0L) {
               return this.jjStopAtPos(15, 303);
            }

            return this.jjMoveStringLiteralDfa16_0(active0, 0L, active1, 1L, active4, 0L, active6, 0L, active8, 0L);
         case 'S':
         case 's':
            if ((active1 & 1152921504606846976L) != 0L) {
               return this.jjStartNfaWithStates_0(15, 124, 300);
            }
            break;
         case 'T':
         case 't':
            if ((active4 & 1073741824L) != 0L) {
               return this.jjStartNfaWithStates_0(15, 286, 300);
            }

            if ((active4 & 8796093022208L) != 0L) {
               return this.jjStartNfaWithStates_0(15, 299, 300);
            }
            break;
         case 'Y':
         case 'y':
            if ((active4 & 32768L) != 0L) {
               return this.jjStopAtPos(15, 271);
            }
         }

         return this.jjStartNfa_0(14, active0, active1, 0L, 0L, active4, 0L, active6, 0L, active8, 0L);
      }
   }

   private int jjMoveStringLiteralDfa16_0(long old0, long active0, long old1, long active1, long old4, long active4, long old6, long active6, long old8, long active8) {
      if (((active0 &= old0) | (active1 &= old1) | active4 & old4 | (active6 &= old6) | (active8 &= old8)) == 0L) {
         return this.jjStartNfa_0(14, old0, old1, 0L, 0L, old4, 0L, old6, 0L, old8, 0L);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var22) {
            this.jjStopStringLiteralDfa_0(15, active0, active1, 0L, 0L, 0L, 0L, active6, 0L, active8, 0L);
            return 16;
         }

         switch(this.curChar) {
         case ' ':
            return this.jjMoveStringLiteralDfa17_0(active0, 0L, active1, 0L, active6, 0L, active8, 64L);
         case 'E':
         case 'e':
            if ((active1 & 281474976710656L) != 0L) {
               return this.jjStartNfaWithStates_0(16, 112, 300);
            }
         default:
            return this.jjStartNfa_0(15, active0, active1, 0L, 0L, 0L, 0L, active6, 0L, active8, 0L);
         case 'K':
         case 'k':
            return this.jjMoveStringLiteralDfa17_0(active0, 8589934592L, active1, 0L, active6, 0L, active8, 0L);
         case 'O':
         case 'o':
            return this.jjMoveStringLiteralDfa17_0(active0, 0L, active1, 1L, active6, 1024L, active8, 0L);
         case 'S':
         case 's':
            return (active6 & 140737488355328L) != 0L ? this.jjStartNfaWithStates_0(16, 431, 300) : this.jjMoveStringLiteralDfa17_0(active0, 0L, active1, 0L, active6, 0L, active8, 128L);
         }
      }
   }

   private int jjMoveStringLiteralDfa17_0(long old0, long active0, long old1, long active1, long old6, long active6, long old8, long active8) {
      if (((active0 &= old0) | (active1 &= old1) | (active6 &= old6) | (active8 &= old8)) == 0L) {
         return this.jjStartNfa_0(15, old0, old1, 0L, 0L, 0L, 0L, old6, 0L, old8, 0L);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var18) {
            this.jjStopStringLiteralDfa_0(16, active0, active1, 0L, 0L, 0L, 0L, active6, 0L, active8, 0L);
            return 17;
         }

         switch(this.curChar) {
         case 'E':
         case 'e':
            return this.jjMoveStringLiteralDfa18_0(active0, 0L, active1, 0L, active6, 0L, active8, 128L);
         case 'M':
         case 'm':
            return this.jjMoveStringLiteralDfa18_0(active0, 0L, active1, 0L, active6, 1024L, active8, 64L);
         case 'S':
         case 's':
            if ((active0 & 8589934592L) != 0L) {
               return this.jjStartNfaWithStates_0(17, 33, 300);
            }
         default:
            return this.jjStartNfa_0(16, active0, active1, 0L, 0L, 0L, 0L, active6, 0L, active8, 0L);
         case 'W':
         case 'w':
            return this.jjMoveStringLiteralDfa18_0(active0, 0L, active1, 1L, active6, 0L, active8, 0L);
         }
      }
   }

   private int jjMoveStringLiteralDfa18_0(long old0, long active0, long old1, long active1, long old6, long active6, long old8, long active8) {
      if ((active0 & old0 | (active1 &= old1) | (active6 &= old6) | (active8 &= old8)) == 0L) {
         return this.jjStartNfa_0(16, old0, old1, 0L, 0L, 0L, 0L, old6, 0L, old8, 0L);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var18) {
            this.jjStopStringLiteralDfa_0(17, 0L, active1, 0L, 0L, 0L, 0L, active6, 0L, active8, 0L);
            return 18;
         }

         switch(this.curChar) {
         case 'C':
         case 'c':
            return this.jjMoveStringLiteralDfa19_0(active1, 0L, active6, 0L, active8, 128L);
         case 'O':
         case 'o':
            return this.jjMoveStringLiteralDfa19_0(active1, 0L, active6, 0L, active8, 64L);
         case 'P':
         case 'p':
            return this.jjMoveStringLiteralDfa19_0(active1, 0L, active6, 1024L, active8, 0L);
         case 'S':
         case 's':
            if ((active1 & 1L) != 0L) {
               return this.jjStartNfaWithStates_0(18, 64, 300);
            }
         default:
            return this.jjStartNfa_0(17, 0L, active1, 0L, 0L, 0L, 0L, active6, 0L, active8, 0L);
         }
      }
   }

   private int jjMoveStringLiteralDfa19_0(long old1, long active1, long old6, long active6, long old8, long active8) {
      if ((active1 & old1 | (active6 &= old6) | (active8 &= old8)) == 0L) {
         return this.jjStartNfa_0(17, 0L, old1, 0L, 0L, 0L, 0L, old6, 0L, old8, 0L);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var14) {
            this.jjStopStringLiteralDfa_0(18, 0L, 0L, 0L, 0L, 0L, 0L, active6, 0L, active8, 0L);
            return 19;
         }

         switch(this.curChar) {
         case 'N':
         case 'n':
            return this.jjMoveStringLiteralDfa20_0(active6, 0L, active8, 64L);
         case 'O':
         case 'o':
            return this.jjMoveStringLiteralDfa20_0(active6, 0L, active8, 128L);
         case 'U':
         case 'u':
            return this.jjMoveStringLiteralDfa20_0(active6, 1024L, active8, 0L);
         default:
            return this.jjStartNfa_0(18, 0L, 0L, 0L, 0L, 0L, 0L, active6, 0L, active8, 0L);
         }
      }
   }

   private int jjMoveStringLiteralDfa20_0(long old6, long active6, long old8, long active8) {
      if (((active6 &= old6) | (active8 &= old8)) == 0L) {
         return this.jjStartNfa_0(18, 0L, 0L, 0L, 0L, 0L, 0L, old6, 0L, old8, 0L);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var10) {
            this.jjStopStringLiteralDfa_0(19, 0L, 0L, 0L, 0L, 0L, 0L, active6, 0L, active8, 0L);
            return 20;
         }

         switch(this.curChar) {
         case 'N':
         case 'n':
            return this.jjMoveStringLiteralDfa21_0(active6, 0L, active8, 128L);
         case 'T':
         case 't':
            return this.jjMoveStringLiteralDfa21_0(active6, 1024L, active8, 64L);
         default:
            return this.jjStartNfa_0(19, 0L, 0L, 0L, 0L, 0L, 0L, active6, 0L, active8, 0L);
         }
      }
   }

   private int jjMoveStringLiteralDfa21_0(long old6, long active6, long old8, long active8) {
      if (((active6 &= old6) | (active8 &= old8)) == 0L) {
         return this.jjStartNfa_0(19, 0L, 0L, 0L, 0L, 0L, 0L, old6, 0L, old8, 0L);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var10) {
            this.jjStopStringLiteralDfa_0(20, 0L, 0L, 0L, 0L, 0L, 0L, active6, 0L, active8, 0L);
            return 21;
         }

         switch(this.curChar) {
         case 'D':
         case 'd':
            if ((active8 & 128L) != 0L) {
               return this.jjStopAtPos(21, 519);
            }
            break;
         case 'E':
         case 'e':
            if ((active6 & 1024L) != 0L) {
               return this.jjStartNfaWithStates_0(21, 394, 300);
            }
            break;
         case 'H':
         case 'h':
            if ((active8 & 64L) != 0L) {
               return this.jjStopAtPos(21, 518);
            }
         }

         return this.jjStartNfa_0(20, 0L, 0L, 0L, 0L, 0L, 0L, active6, 0L, active8, 0L);
      }
   }

   private int jjStartNfaWithStates_0(int pos, int kind, int state) {
      this.jjmatchedKind = kind;
      this.jjmatchedPos = pos;

      try {
         this.curChar = this.input_stream.readChar();
      } catch (IOException var5) {
         return pos + 1;
      }

      return this.jjMoveNfa_0(state, pos + 1);
   }

   private int jjMoveNfa_0(int startState, int curPos) {
      int startsAt = 0;
      this.jjnewStateCnt = 299;
      int i = 1;
      this.jjstateSet[0] = startState;
      int kind = Integer.MAX_VALUE;

      while(true) {
         if (++this.jjround == Integer.MAX_VALUE) {
            this.ReInitRounds();
         }

         long l;
         if (this.curChar < '@') {
            l = 1L << this.curChar;

            do {
               --i;
               switch(this.jjstateSet[i]) {
               case 0:
                  if (this.curChar == '-') {
                     if (kind > 5) {
                        kind = 5;
                     }

                     this.jjCheckNAdd(1);
                  }
                  break;
               case 1:
                  if ((-9217L & l) != 0L) {
                     if (kind > 5) {
                        kind = 5;
                     }

                     this.jjCheckNAdd(1);
                  }
                  break;
               case 2:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 558) {
                        kind = 558;
                     }

                     this.jjCheckNAddStates(30, 40);
                  } else if ((103079215104L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAddTwoStates(16, 17);
                  } else if (this.curChar == ':') {
                     this.jjCheckNAddStates(41, 53);
                  } else if (this.curChar == '/') {
                     this.jjAddStates(54, 58);
                  } else if (this.curChar == '.') {
                     this.jjCheckNAddTwoStates(73, 74);
                  } else if (this.curChar == '\'') {
                     this.jjCheckNAddTwoStates(51, 52);
                  } else if (this.curChar == '"') {
                     this.jjCheckNAddTwoStates(19, 20);
                  } else if (this.curChar == '&') {
                     this.jjCheckNAdd(13);
                  } else if (this.curChar == '-') {
                     this.jjstateSet[this.jjnewStateCnt++] = 0;
                  } else if (this.curChar == '?') {
                     this.jjCheckNAdd(15);
                  }

                  if (this.curChar == '$') {
                     this.jjAddStates(59, 63);
                  } else if (this.curChar == '0') {
                     this.jjstateSet[this.jjnewStateCnt++] = 70;
                  } else if ((-9223371761976868864L & l) != 0L && kind > 544) {
                     kind = 544;
                  }
               case 3:
               case 6:
               case 7:
               case 10:
               case 21:
               case 41:
               case 49:
               case 55:
               case 61:
               case 63:
               case 64:
               case 66:
               case 68:
               case 70:
               case 75:
               case 124:
               case 126:
               case 162:
               case 167:
               case 181:
               case 209:
               case 213:
               case 220:
               case 246:
               case 272:
               case 295:
               case 296:
               case 297:
               case 298:
               default:
                  break;
               case 4:
                  if ((287949004254216192L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAdd(17);
                  }

                  if ((103079215104L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAddTwoStates(16, 17);
                  }
                  break;
               case 5:
                  if ((287949004254216192L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAdd(17);
                  }

                  if ((103079215104L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAddTwoStates(16, 17);
                  }
                  break;
               case 8:
                  if ((287949004254216192L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAdd(17);
                  }

                  if ((103079215104L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAddTwoStates(16, 17);
                  }
                  break;
               case 9:
                  if ((287949004254216192L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAdd(17);
                  }

                  if ((103079215104L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAddTwoStates(16, 17);
                  }
                  break;
               case 11:
                  if ((-9223371761976868864L & l) != 0L && kind > 544) {
                     kind = 544;
                  }
                  break;
               case 12:
                  if (this.curChar == '&') {
                     this.jjCheckNAdd(13);
                  }
                  break;
               case 13:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAdd(13);
                  }
                  break;
               case 14:
                  if (this.curChar == '?') {
                     this.jjCheckNAdd(15);
                  }
                  break;
               case 15:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAdd(15);
                  }
                  break;
               case 16:
                  if ((103079215104L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAddTwoStates(16, 17);
                  }
                  break;
               case 17:
                  if ((287949004254216192L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAdd(17);
                  }
                  break;
               case 18:
                  if (this.curChar == '"') {
                     this.jjCheckNAddTwoStates(19, 20);
                  }
                  break;
               case 19:
                  if ((-17179869185L & l) != 0L) {
                     this.jjCheckNAddTwoStates(19, 20);
                  }
                  break;
               case 20:
                  if (this.curChar == '"' && kind > 547) {
                     kind = 547;
                  }
                  break;
               case 22:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(0, 10);
                  } else if ((103079215104L & l) != 0L) {
                     if (kind > 549) {
                        kind = 549;
                     }

                     this.jjCheckNAddStates(64, 66);
                  } else if (this.curChar == '.') {
                     this.jjCheckNAddTwoStates(269, 271);
                  } else if ((-9223371761976868864L & l) != 0L) {
                     if (kind > 549) {
                        kind = 549;
                     }

                     this.jjCheckNAdd(25);
                  } else if (this.curChar == '"') {
                     this.jjCheckNAddTwoStates(23, 24);
                  }

                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddStates(14, 21);
                  } else if ((103079215104L & l) != 0L) {
                     if (kind > 548) {
                        kind = 548;
                     }

                     this.jjCheckNAddStates(11, 13);
                  } else if (this.curChar == '.') {
                     this.jjCheckNAddTwoStates(243, 245);
                  } else if (this.curChar == '&') {
                     this.jjCheckNAdd(40);
                  } else if (this.curChar == '"') {
                     this.jjCheckNAddTwoStates(141, 142);
                  } else if (this.curChar == '?') {
                     this.jjCheckNAdd(38);
                  }

                  if (this.curChar == '$') {
                     this.jjAddStates(22, 25);
                  } else if (this.curChar == '0') {
                     this.jjstateSet[this.jjnewStateCnt++] = 209;
                  } else if ((-9223371761976868864L & l) != 0L) {
                     if (kind > 548) {
                        kind = 548;
                     }

                     this.jjCheckNAdd(143);
                  }

                  if (this.curChar == '$') {
                     this.jjAddStates(26, 29);
                  } else if (this.curChar == '0') {
                     this.jjstateSet[this.jjnewStateCnt++] = 162;
                  } else if (this.curChar == '&') {
                     this.jjCheckNAdd(159);
                  } else if (this.curChar == '?') {
                     this.jjCheckNAdd(157);
                  }
                  break;
               case 23:
                  if ((-17179869185L & l) != 0L) {
                     this.jjCheckNAddTwoStates(23, 24);
                  }
                  break;
               case 24:
                  if (this.curChar == '"') {
                     if (kind > 549) {
                        kind = 549;
                     }

                     this.jjCheckNAdd(25);
                  }
                  break;
               case 25:
                  if (this.curChar == '.') {
                     this.jjCheckNAddStates(67, 71);
                  }
                  break;
               case 26:
                  if (this.curChar == '"') {
                     this.jjCheckNAddTwoStates(27, 24);
                  }
                  break;
               case 27:
                  if ((-17179869185L & l) != 0L) {
                     this.jjCheckNAddTwoStates(27, 24);
                  }
                  break;
               case 28:
                  if ((103079215104L & l) != 0L) {
                     if (kind > 549) {
                        kind = 549;
                     }

                     this.jjCheckNAddStates(72, 74);
                  }
                  break;
               case 29:
                  if ((287949004254216192L & l) != 0L) {
                     if (kind > 549) {
                        kind = 549;
                     }

                     this.jjCheckNAddTwoStates(25, 29);
                  }
                  break;
               case 30:
                  if (this.curChar == '?') {
                     this.jjCheckNAdd(31);
                  }
                  break;
               case 31:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 549) {
                        kind = 549;
                     }

                     this.jjCheckNAddTwoStates(25, 31);
                  }
                  break;
               case 32:
                  if (this.curChar == '&') {
                     this.jjCheckNAdd(33);
                  }
                  break;
               case 33:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 549) {
                        kind = 549;
                     }

                     this.jjCheckNAddTwoStates(25, 33);
                  }
                  break;
               case 34:
                  if ((-9223371761976868864L & l) != 0L) {
                     if (kind > 549) {
                        kind = 549;
                     }

                     this.jjCheckNAdd(25);
                  }
                  break;
               case 35:
                  if ((103079215104L & l) != 0L) {
                     if (kind > 549) {
                        kind = 549;
                     }

                     this.jjCheckNAddStates(64, 66);
                  }
                  break;
               case 36:
                  if ((287949004254216192L & l) != 0L) {
                     if (kind > 549) {
                        kind = 549;
                     }

                     this.jjCheckNAddTwoStates(36, 25);
                  }
                  break;
               case 37:
                  if (this.curChar == '?') {
                     this.jjCheckNAdd(38);
                  }
                  break;
               case 38:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 549) {
                        kind = 549;
                     }

                     this.jjCheckNAddTwoStates(38, 25);
                  }
                  break;
               case 39:
                  if (this.curChar == '&') {
                     this.jjCheckNAdd(40);
                  }
                  break;
               case 40:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 549) {
                        kind = 549;
                     }

                     this.jjCheckNAddTwoStates(40, 25);
                  }
                  break;
               case 42:
                  if ((-9223371761976868864L & l) != 0L && kind > 552) {
                     kind = 552;
                  }
                  break;
               case 43:
                  if (this.curChar == '&') {
                     this.jjCheckNAdd(44);
                  }
                  break;
               case 44:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 552) {
                        kind = 552;
                     }

                     this.jjCheckNAdd(44);
                  }
                  break;
               case 45:
                  if (this.curChar == '?') {
                     this.jjCheckNAdd(46);
                  }
                  break;
               case 46:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 552) {
                        kind = 552;
                     }

                     this.jjCheckNAdd(46);
                  }
                  break;
               case 47:
                  if ((103079215104L & l) != 0L) {
                     if (kind > 552) {
                        kind = 552;
                     }

                     this.jjCheckNAddTwoStates(47, 48);
                  }
                  break;
               case 48:
                  if ((287949004254216192L & l) != 0L) {
                     if (kind > 552) {
                        kind = 552;
                     }

                     this.jjCheckNAdd(48);
                  }
                  break;
               case 50:
                  if (this.curChar == '\'') {
                     this.jjCheckNAddTwoStates(51, 52);
                  }
                  break;
               case 51:
                  if ((-549755813889L & l) != 0L) {
                     this.jjCheckNAddTwoStates(51, 52);
                  }
                  break;
               case 52:
                  if (this.curChar == '\'') {
                     if (kind > 553) {
                        kind = 553;
                     }

                     this.jjstateSet[this.jjnewStateCnt++] = 53;
                  }
                  break;
               case 53:
                  if (this.curChar == '\'') {
                     this.jjCheckNAddTwoStates(54, 52);
                  }
                  break;
               case 54:
                  if ((-549755813889L & l) != 0L) {
                     this.jjCheckNAddTwoStates(54, 52);
                  }
                  break;
               case 56:
                  if ((287949004254216192L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAdd(17);
                  } else if (this.curChar == '\'') {
                     this.jjCheckNAddTwoStates(57, 58);
                  }

                  if ((103079215104L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAddTwoStates(16, 17);
                  }
                  break;
               case 57:
                  if ((-549755813889L & l) != 0L) {
                     this.jjCheckNAddTwoStates(57, 58);
                  }
                  break;
               case 58:
                  if (this.curChar == '\'') {
                     if (kind > 553) {
                        kind = 553;
                     }

                     this.jjstateSet[this.jjnewStateCnt++] = 59;
                  }
                  break;
               case 59:
                  if (this.curChar == '\'') {
                     this.jjCheckNAddTwoStates(60, 58);
                  }
                  break;
               case 60:
                  if ((-549755813889L & l) != 0L) {
                     this.jjCheckNAddTwoStates(60, 58);
                  }
                  break;
               case 62:
                  this.jjCheckNAddTwoStates(62, 63);
                  break;
               case 65:
                  this.jjCheckNAddTwoStates(65, 63);
                  break;
               case 67:
               case 301:
                  this.jjCheckNAddTwoStates(67, 68);
                  break;
               case 69:
                  if (this.curChar == '0') {
                     this.jjstateSet[this.jjnewStateCnt++] = 70;
                  }
                  break;
               case 71:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 558) {
                        kind = 558;
                     }

                     this.jjstateSet[this.jjnewStateCnt++] = 71;
                  }
                  break;
               case 72:
                  if (this.curChar == '.') {
                     this.jjCheckNAddTwoStates(73, 74);
                  }
                  break;
               case 73:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 558) {
                        kind = 558;
                     }

                     this.jjCheckNAdd(73);
                  }
                  break;
               case 74:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 558) {
                        kind = 558;
                     }

                     this.jjCheckNAddTwoStates(74, 75);
                  }
                  break;
               case 76:
                  if ((43980465111040L & l) != 0L) {
                     this.jjAddStates(75, 76);
                  }
                  break;
               case 77:
                  if (this.curChar == '.') {
                     this.jjCheckNAdd(78);
                  }
                  break;
               case 78:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 558) {
                        kind = 558;
                     }

                     this.jjCheckNAdd(78);
                  }
                  break;
               case 79:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 558) {
                        kind = 558;
                     }

                     this.jjCheckNAddStates(77, 79);
                  }
                  break;
               case 80:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 558) {
                        kind = 558;
                     }

                     this.jjCheckNAdd(80);
                  }
                  break;
               case 81:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 558) {
                        kind = 558;
                     }

                     this.jjCheckNAddTwoStates(81, 82);
                  }
                  break;
               case 82:
                  if (this.curChar == '.') {
                     if (kind > 558) {
                        kind = 558;
                     }

                     this.jjCheckNAdd(83);
                  }
                  break;
               case 83:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 558) {
                        kind = 558;
                     }

                     this.jjCheckNAdd(83);
                  }
                  break;
               case 84:
                  if (this.curChar == '/') {
                     this.jjAddStates(54, 58);
                  }
                  break;
               case 85:
                  if (this.curChar == '*') {
                     this.jjstateSet[this.jjnewStateCnt++] = 117;
                  }

                  if (this.curChar == '*') {
                     this.jjstateSet[this.jjnewStateCnt++] = 108;
                  }

                  if (this.curChar == '*') {
                     this.jjCheckNAddTwoStates(102, 103);
                  }

                  if (this.curChar == '*') {
                     this.jjstateSet[this.jjnewStateCnt++] = 93;
                  }

                  if (this.curChar == '*') {
                     this.jjstateSet[this.jjnewStateCnt++] = 86;
                  }
                  break;
               case 86:
                  if (this.curChar == '+') {
                     this.jjCheckNAddTwoStates(87, 88);
                  }
                  break;
               case 87:
                  if ((-4398046511105L & l) != 0L) {
                     this.jjCheckNAddTwoStates(87, 88);
                  }
                  break;
               case 88:
                  if (this.curChar == '*') {
                     this.jjCheckNAddStates(80, 82);
                  }
                  break;
               case 89:
                  if ((-145135534866433L & l) != 0L) {
                     this.jjCheckNAddTwoStates(90, 88);
                  }
                  break;
               case 90:
                  if ((-4398046511105L & l) != 0L) {
                     this.jjCheckNAddTwoStates(90, 88);
                  }
                  break;
               case 91:
                  if (this.curChar == '/' && kind > 6) {
                     kind = 6;
                  }
                  break;
               case 92:
                  if (this.curChar == '*') {
                     this.jjstateSet[this.jjnewStateCnt++] = 93;
                  }
                  break;
               case 93:
                  if (this.curChar == '%') {
                     this.jjCheckNAdd(94);
                  }
                  break;
               case 94:
                  if ((103079215104L & l) != 0L) {
                     this.jjCheckNAddTwoStates(94, 95);
                  }
                  break;
               case 95:
                  if (this.curChar == '%') {
                     this.jjCheckNAddTwoStates(96, 97);
                  }
                  break;
               case 96:
                  if ((-4398046511105L & l) != 0L) {
                     this.jjCheckNAddTwoStates(96, 97);
                  }
                  break;
               case 97:
                  if (this.curChar == '*') {
                     this.jjCheckNAddStates(83, 85);
                  }
                  break;
               case 98:
                  if ((-145135534866433L & l) != 0L) {
                     this.jjCheckNAddTwoStates(99, 97);
                  }
                  break;
               case 99:
                  if ((-4398046511105L & l) != 0L) {
                     this.jjCheckNAddTwoStates(99, 97);
                  }
                  break;
               case 100:
                  if (this.curChar == '/' && kind > 7) {
                     kind = 7;
                  }
                  break;
               case 101:
                  if (this.curChar == '*') {
                     this.jjCheckNAddTwoStates(102, 103);
                  }
                  break;
               case 102:
                  if ((-4398046511105L & l) != 0L) {
                     this.jjCheckNAddTwoStates(102, 103);
                  }
                  break;
               case 103:
                  if (this.curChar == '*') {
                     this.jjCheckNAddStates(86, 88);
                  }
                  break;
               case 104:
                  if ((-145135534866433L & l) != 0L) {
                     this.jjCheckNAddTwoStates(105, 103);
                  }
                  break;
               case 105:
                  if ((-4398046511105L & l) != 0L) {
                     this.jjCheckNAddTwoStates(105, 103);
                  }
                  break;
               case 106:
                  if (this.curChar == '/' && kind > 8) {
                     kind = 8;
                  }
                  break;
               case 107:
                  if (this.curChar == '*') {
                     this.jjstateSet[this.jjnewStateCnt++] = 108;
                  }
                  break;
               case 108:
                  if (this.curChar == '%') {
                     this.jjCheckNAdd(109);
                  }
                  break;
               case 109:
                  if ((103079215104L & l) != 0L) {
                     this.jjCheckNAddTwoStates(109, 110);
                  }
                  break;
               case 110:
                  if (this.curChar == '%') {
                     this.jjCheckNAddTwoStates(111, 112);
                  }
                  break;
               case 111:
                  if ((-4398046511105L & l) != 0L) {
                     this.jjCheckNAddTwoStates(111, 112);
                  }
                  break;
               case 112:
                  if (this.curChar == '*') {
                     this.jjCheckNAddStates(89, 91);
                  }
                  break;
               case 113:
                  if ((-145135534866433L & l) != 0L) {
                     this.jjCheckNAddTwoStates(114, 112);
                  }
                  break;
               case 114:
                  if ((-4398046511105L & l) != 0L) {
                     this.jjCheckNAddTwoStates(114, 112);
                  }
                  break;
               case 115:
                  if (this.curChar == '/' && kind > 556) {
                     kind = 556;
                  }
                  break;
               case 116:
                  if (this.curChar == '*') {
                     this.jjstateSet[this.jjnewStateCnt++] = 117;
                  }
                  break;
               case 117:
                  if (this.curChar == '+') {
                     this.jjCheckNAddTwoStates(118, 119);
                  }
                  break;
               case 118:
                  if ((-4398046511105L & l) != 0L) {
                     this.jjCheckNAddTwoStates(118, 119);
                  }
                  break;
               case 119:
                  if (this.curChar == '*') {
                     this.jjCheckNAddStates(92, 94);
                  }
                  break;
               case 120:
                  if ((-145135534866433L & l) != 0L) {
                     this.jjCheckNAddTwoStates(121, 119);
                  }
                  break;
               case 121:
                  if ((-4398046511105L & l) != 0L) {
                     this.jjCheckNAddTwoStates(121, 119);
                  }
                  break;
               case 122:
                  if (this.curChar == '/' && kind > 557) {
                     kind = 557;
                  }
                  break;
               case 123:
                  if (this.curChar == '$') {
                     this.jjAddStates(59, 63);
                  }
                  break;
               case 125:
                  this.jjAddStates(95, 96);
                  break;
               case 127:
                  if (this.curChar == '.') {
                     this.jjCheckNAdd(73);
                  }
                  break;
               case 128:
                  if (this.curChar == '.') {
                     this.jjCheckNAdd(74);
                  }
                  break;
               case 129:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 558) {
                        kind = 558;
                     }

                     this.jjCheckNAddStates(97, 100);
                  }
                  break;
               case 130:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 558) {
                        kind = 558;
                     }

                     this.jjCheckNAddTwoStates(130, 75);
                  }
                  break;
               case 131:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 558) {
                        kind = 558;
                     }

                     this.jjCheckNAddStates(101, 103);
                  }
                  break;
               case 132:
                  if (this.curChar == '.') {
                     if (kind > 558) {
                        kind = 558;
                     }

                     this.jjCheckNAddTwoStates(133, 75);
                  }
                  break;
               case 133:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 558) {
                        kind = 558;
                     }

                     this.jjCheckNAddTwoStates(133, 75);
                  }
                  break;
               case 134:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 558) {
                        kind = 558;
                     }

                     this.jjCheckNAddStates(104, 106);
                  }
                  break;
               case 135:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 558) {
                        kind = 558;
                     }

                     this.jjCheckNAdd(135);
                  }
                  break;
               case 136:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 558) {
                        kind = 558;
                     }

                     this.jjCheckNAddTwoStates(136, 137);
                  }
                  break;
               case 137:
                  if (this.curChar == '.') {
                     if (kind > 558) {
                        kind = 558;
                     }

                     this.jjCheckNAdd(138);
                  }
                  break;
               case 138:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 558) {
                        kind = 558;
                     }

                     this.jjCheckNAdd(138);
                  }
                  break;
               case 139:
                  if (this.curChar == ':') {
                     this.jjCheckNAddStates(41, 53);
                  }
                  break;
               case 140:
                  if (this.curChar == '"') {
                     this.jjCheckNAddTwoStates(141, 142);
                  }
                  break;
               case 141:
                  if ((-17179869185L & l) != 0L) {
                     this.jjCheckNAddTwoStates(141, 142);
                  }
                  break;
               case 142:
                  if (this.curChar == '"') {
                     if (kind > 548) {
                        kind = 548;
                     }

                     this.jjCheckNAdd(143);
                  }
                  break;
               case 143:
                  if (this.curChar == '.') {
                     this.jjCheckNAddStates(107, 111);
                  }
                  break;
               case 144:
                  if (this.curChar == '"') {
                     this.jjCheckNAddTwoStates(145, 146);
                  }
                  break;
               case 145:
                  if ((-17179869185L & l) != 0L) {
                     this.jjCheckNAddTwoStates(145, 146);
                  }
                  break;
               case 146:
                  if (this.curChar == '"' && kind > 548) {
                     kind = 548;
                  }
                  break;
               case 147:
                  if ((103079215104L & l) != 0L) {
                     if (kind > 548) {
                        kind = 548;
                     }

                     this.jjCheckNAddTwoStates(147, 148);
                  }
                  break;
               case 148:
                  if ((287949004254216192L & l) != 0L) {
                     if (kind > 548) {
                        kind = 548;
                     }

                     this.jjCheckNAdd(148);
                  }
                  break;
               case 149:
                  if (this.curChar == '?') {
                     this.jjCheckNAdd(150);
                  }
                  break;
               case 150:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 548) {
                        kind = 548;
                     }

                     this.jjCheckNAdd(150);
                  }
                  break;
               case 151:
                  if (this.curChar == '&') {
                     this.jjCheckNAdd(152);
                  }
                  break;
               case 152:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 548) {
                        kind = 548;
                     }

                     this.jjCheckNAdd(152);
                  }
                  break;
               case 153:
                  if ((-9223371761976868864L & l) != 0L && kind > 548) {
                     kind = 548;
                  }
                  break;
               case 154:
                  if ((103079215104L & l) != 0L) {
                     if (kind > 548) {
                        kind = 548;
                     }

                     this.jjCheckNAddStates(11, 13);
                  }
                  break;
               case 155:
                  if ((287949004254216192L & l) != 0L) {
                     if (kind > 548) {
                        kind = 548;
                     }

                     this.jjCheckNAddTwoStates(155, 143);
                  }
                  break;
               case 156:
                  if (this.curChar == '?') {
                     this.jjCheckNAdd(157);
                  }
                  break;
               case 157:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 548) {
                        kind = 548;
                     }

                     this.jjCheckNAddTwoStates(157, 143);
                  }
                  break;
               case 158:
                  if (this.curChar == '&') {
                     this.jjCheckNAdd(159);
                  }
                  break;
               case 159:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 548) {
                        kind = 548;
                     }

                     this.jjCheckNAddTwoStates(159, 143);
                  }
                  break;
               case 160:
                  if ((-9223371761976868864L & l) != 0L) {
                     if (kind > 548) {
                        kind = 548;
                     }

                     this.jjCheckNAdd(143);
                  }
                  break;
               case 161:
                  if (this.curChar == '0') {
                     this.jjstateSet[this.jjnewStateCnt++] = 162;
                  }
                  break;
               case 163:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(112, 116);
                  }
                  break;
               case 164:
                  if ((-9223371761976868864L & l) != 0L) {
                     if (kind > 550) {
                        kind = 550;
                     }

                     this.jjCheckNAdd(165);
                  }
                  break;
               case 165:
                  if (this.curChar == '.') {
                     this.jjAddStates(117, 120);
                  }
                  break;
               case 166:
                  if (this.curChar == '0') {
                     this.jjstateSet[this.jjnewStateCnt++] = 167;
                  }
                  break;
               case 168:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(121, 125);
                  }
                  break;
               case 169:
                  if ((-9223371761976868864L & l) != 0L && kind > 550) {
                     kind = 550;
                  }
                  break;
               case 170:
                  if (this.curChar == '&') {
                     this.jjCheckNAdd(171);
                  }
                  break;
               case 171:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 550) {
                        kind = 550;
                     }

                     this.jjCheckNAdd(171);
                  }
                  break;
               case 172:
                  if (this.curChar == '?') {
                     this.jjCheckNAdd(173);
                  }
                  break;
               case 173:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 550) {
                        kind = 550;
                     }

                     this.jjCheckNAdd(173);
                  }
                  break;
               case 174:
                  if ((103079215104L & l) != 0L) {
                     if (kind > 550) {
                        kind = 550;
                     }

                     this.jjCheckNAddTwoStates(174, 175);
                  }
                  break;
               case 175:
                  if ((287949004254216192L & l) != 0L) {
                     if (kind > 550) {
                        kind = 550;
                     }

                     this.jjCheckNAdd(175);
                  }
                  break;
               case 176:
                  if (this.curChar == '$') {
                     this.jjAddStates(126, 129);
                  }
                  break;
               case 177:
                  if (this.curChar == '.') {
                     this.jjCheckNAdd(178);
                  }
                  break;
               case 178:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(130, 134);
                  }
                  break;
               case 179:
                  if (this.curChar == '.') {
                     this.jjCheckNAdd(180);
                  }
                  break;
               case 180:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(135, 140);
                  }
                  break;
               case 182:
                  if ((43980465111040L & l) != 0L) {
                     this.jjAddStates(141, 142);
                  }
                  break;
               case 183:
                  if (this.curChar == '.') {
                     this.jjCheckNAdd(184);
                  }
                  break;
               case 184:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(143, 147);
                  }
                  break;
               case 185:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(148, 154);
                  }
                  break;
               case 186:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(155, 159);
                  }
                  break;
               case 187:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(160, 165);
                  }
                  break;
               case 188:
                  if (this.curChar == '.') {
                     this.jjCheckNAddStates(166, 170);
                  }
                  break;
               case 189:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(166, 170);
                  }
                  break;
               case 190:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(171, 178);
                  }
                  break;
               case 191:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(179, 184);
                  }
                  break;
               case 192:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(185, 191);
                  }
                  break;
               case 193:
                  if (this.curChar == '.') {
                     this.jjCheckNAddStates(192, 197);
                  }
                  break;
               case 194:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(192, 197);
                  }
                  break;
               case 195:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(198, 204);
                  }
                  break;
               case 196:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(205, 209);
                  }
                  break;
               case 197:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(210, 215);
                  }
                  break;
               case 198:
                  if (this.curChar == '.') {
                     this.jjCheckNAddStates(216, 220);
                  }
                  break;
               case 199:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(216, 220);
                  }
                  break;
               case 200:
                  if (this.curChar == '.') {
                     this.jjCheckNAddTwoStates(178, 180);
                  }
                  break;
               case 201:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(221, 231);
                  }
                  break;
               case 202:
                  if (this.curChar == '&') {
                     this.jjCheckNAdd(203);
                  }
                  break;
               case 203:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 550) {
                        kind = 550;
                     }

                     this.jjCheckNAddTwoStates(203, 165);
                  }
                  break;
               case 204:
                  if (this.curChar == '?') {
                     this.jjCheckNAdd(205);
                  }
                  break;
               case 205:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 550) {
                        kind = 550;
                     }

                     this.jjCheckNAddTwoStates(205, 165);
                  }
                  break;
               case 206:
                  if ((103079215104L & l) != 0L) {
                     if (kind > 550) {
                        kind = 550;
                     }

                     this.jjCheckNAddStates(232, 234);
                  }
                  break;
               case 207:
                  if ((287949004254216192L & l) != 0L) {
                     if (kind > 550) {
                        kind = 550;
                     }

                     this.jjCheckNAddTwoStates(207, 165);
                  }
                  break;
               case 208:
                  if (this.curChar == '0') {
                     this.jjstateSet[this.jjnewStateCnt++] = 209;
                  }
                  break;
               case 210:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddTwoStates(210, 211);
                  }
                  break;
               case 211:
                  if (this.curChar == '.') {
                     this.jjCheckNAddStates(235, 238);
                  }
                  break;
               case 212:
                  if (this.curChar == '0') {
                     this.jjstateSet[this.jjnewStateCnt++] = 213;
                  }
                  break;
               case 214:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjstateSet[this.jjnewStateCnt++] = 214;
                  }
                  break;
               case 215:
                  if (this.curChar == '$') {
                     this.jjAddStates(239, 242);
                  }
                  break;
               case 216:
                  if (this.curChar == '.') {
                     this.jjCheckNAdd(217);
                  }
                  break;
               case 217:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAdd(217);
                  }
                  break;
               case 218:
                  if (this.curChar == '.') {
                     this.jjCheckNAdd(219);
                  }
                  break;
               case 219:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddTwoStates(219, 220);
                  }
                  break;
               case 221:
                  if ((43980465111040L & l) != 0L) {
                     this.jjAddStates(243, 244);
                  }
                  break;
               case 222:
                  if (this.curChar == '.') {
                     this.jjCheckNAdd(223);
                  }
                  break;
               case 223:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAdd(223);
                  }
                  break;
               case 224:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddStates(245, 247);
                  }
                  break;
               case 225:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAdd(225);
                  }
                  break;
               case 226:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddTwoStates(226, 227);
                  }
                  break;
               case 227:
                  if (this.curChar == '.') {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAdd(228);
                  }
                  break;
               case 228:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAdd(228);
                  }
                  break;
               case 229:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddStates(248, 251);
                  }
                  break;
               case 230:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddTwoStates(230, 220);
                  }
                  break;
               case 231:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddStates(252, 254);
                  }
                  break;
               case 232:
                  if (this.curChar == '.') {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddTwoStates(233, 220);
                  }
                  break;
               case 233:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddTwoStates(233, 220);
                  }
                  break;
               case 234:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddStates(255, 257);
                  }
                  break;
               case 235:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAdd(235);
                  }
                  break;
               case 236:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddTwoStates(236, 237);
                  }
                  break;
               case 237:
                  if (this.curChar == '.') {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAdd(238);
                  }
                  break;
               case 238:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAdd(238);
                  }
                  break;
               case 239:
                  if (this.curChar == '.') {
                     this.jjCheckNAddTwoStates(217, 219);
                  }
                  break;
               case 240:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddStates(258, 264);
                  }
                  break;
               case 241:
                  if (this.curChar == '$') {
                     this.jjAddStates(26, 29);
                  }
                  break;
               case 242:
                  if (this.curChar == '.') {
                     this.jjCheckNAdd(243);
                  }
                  break;
               case 243:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddTwoStates(243, 211);
                  }
                  break;
               case 244:
                  if (this.curChar == '.') {
                     this.jjCheckNAdd(245);
                  }
                  break;
               case 245:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddStates(265, 267);
                  }
                  break;
               case 247:
                  if ((43980465111040L & l) != 0L) {
                     this.jjAddStates(268, 269);
                  }
                  break;
               case 248:
                  if (this.curChar == '.') {
                     this.jjCheckNAdd(249);
                  }
                  break;
               case 249:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddTwoStates(249, 211);
                  }
                  break;
               case 250:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddStates(270, 273);
                  }
                  break;
               case 251:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddTwoStates(251, 211);
                  }
                  break;
               case 252:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddTwoStates(252, 253);
                  }
                  break;
               case 253:
                  if (this.curChar == '.') {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddStates(274, 279);
                  }
                  break;
               case 254:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddTwoStates(254, 211);
                  }
                  break;
               case 255:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddStates(280, 284);
                  }
                  break;
               case 256:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddStates(285, 287);
                  }
                  break;
               case 257:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddStates(288, 290);
                  }
                  break;
               case 258:
                  if (this.curChar == '.') {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddStates(291, 297);
                  }
                  break;
               case 259:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddStates(298, 300);
                  }
                  break;
               case 260:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddStates(301, 304);
                  }
                  break;
               case 261:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddTwoStates(261, 211);
                  }
                  break;
               case 262:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddTwoStates(262, 263);
                  }
                  break;
               case 263:
                  if (this.curChar == '.') {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddStates(305, 310);
                  }
                  break;
               case 264:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddTwoStates(264, 211);
                  }
                  break;
               case 265:
                  if (this.curChar == '.') {
                     this.jjCheckNAddTwoStates(243, 245);
                  }
                  break;
               case 266:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddStates(14, 21);
                  }
                  break;
               case 267:
                  if (this.curChar == '$') {
                     this.jjAddStates(22, 25);
                  }
                  break;
               case 268:
                  if (this.curChar == '.') {
                     this.jjCheckNAdd(269);
                  }
                  break;
               case 269:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(311, 315);
                  }
                  break;
               case 270:
                  if (this.curChar == '.') {
                     this.jjCheckNAdd(271);
                  }
                  break;
               case 271:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(316, 321);
                  }
                  break;
               case 273:
                  if ((43980465111040L & l) != 0L) {
                     this.jjAddStates(322, 323);
                  }
                  break;
               case 274:
                  if (this.curChar == '.') {
                     this.jjCheckNAdd(275);
                  }
                  break;
               case 275:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(324, 328);
                  }
                  break;
               case 276:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(329, 335);
                  }
                  break;
               case 277:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(336, 340);
                  }
                  break;
               case 278:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(341, 346);
                  }
                  break;
               case 279:
                  if (this.curChar == '.') {
                     this.jjCheckNAddStates(347, 351);
                  }
                  break;
               case 280:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(347, 351);
                  }
                  break;
               case 281:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(352, 359);
                  }
                  break;
               case 282:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(360, 365);
                  }
                  break;
               case 283:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(366, 372);
                  }
                  break;
               case 284:
                  if (this.curChar == '.') {
                     this.jjCheckNAddStates(373, 378);
                  }
                  break;
               case 285:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(373, 378);
                  }
                  break;
               case 286:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(379, 385);
                  }
                  break;
               case 287:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(386, 390);
                  }
                  break;
               case 288:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(391, 396);
                  }
                  break;
               case 289:
                  if (this.curChar == '.') {
                     this.jjCheckNAddStates(397, 401);
                  }
                  break;
               case 290:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(397, 401);
                  }
                  break;
               case 291:
                  if (this.curChar == '.') {
                     this.jjCheckNAddTwoStates(269, 271);
                  }
                  break;
               case 292:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(0, 10);
                  }
                  break;
               case 293:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 558) {
                        kind = 558;
                     }

                     this.jjCheckNAddStates(30, 40);
                  }
                  break;
               case 294:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(402, 405);
                  }
                  break;
               case 299:
                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 558) {
                        kind = 558;
                     }

                     this.jjCheckNAddTwoStates(74, 75);
                  }

                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 558) {
                        kind = 558;
                     }

                     this.jjCheckNAdd(73);
                  }
                  break;
               case 300:
                  if ((287949004254216192L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAdd(17);
                  }

                  if ((103079215104L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAddTwoStates(16, 17);
                  }
                  break;
               case 302:
                  if ((287948901175001088L & l) != 0L) {
                     this.jjCheckNAddStates(0, 10);
                  } else if ((103079215104L & l) != 0L) {
                     if (kind > 548) {
                        kind = 548;
                     }

                     this.jjCheckNAddStates(11, 13);
                  } else if (this.curChar == '.') {
                     this.jjCheckNAddTwoStates(269, 271);
                  } else if ((-9223371761976868864L & l) != 0L) {
                     if (kind > 548) {
                        kind = 548;
                     }

                     this.jjCheckNAdd(143);
                  } else if (this.curChar == '"') {
                     this.jjCheckNAddTwoStates(141, 142);
                  }

                  if ((287948901175001088L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddStates(14, 21);
                  } else if (this.curChar == '$') {
                     this.jjAddStates(22, 25);
                  } else if (this.curChar == '.') {
                     this.jjCheckNAddTwoStates(243, 245);
                  } else if (this.curChar == '&') {
                     this.jjCheckNAdd(159);
                  } else if (this.curChar == '?') {
                     this.jjCheckNAdd(157);
                  }

                  if (this.curChar == '$') {
                     this.jjAddStates(26, 29);
                  } else if (this.curChar == '0') {
                     this.jjstateSet[this.jjnewStateCnt++] = 209;
                  }

                  if (this.curChar == '0') {
                     this.jjstateSet[this.jjnewStateCnt++] = 162;
                  }
                  break;
               case 303:
                  if ((287949004254216192L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAdd(17);
                  }

                  if ((103079215104L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAddTwoStates(16, 17);
                  }
               }
            } while(i != startsAt);
         } else if (this.curChar < 128) {
            l = 1L << (this.curChar & 63);

            do {
               --i;
               switch(this.jjstateSet[i]) {
               case 1:
                  if (kind > 5) {
                     kind = 5;
                  }

                  this.jjstateSet[this.jjnewStateCnt++] = 1;
                  break;
               case 2:
                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAddTwoStates(16, 17);
                  } else if (this.curChar == '@') {
                     this.jjCheckNAddStates(41, 53);
                  } else if (this.curChar == '[') {
                     this.jjCheckNAddTwoStates(67, 68);
                  } else if (this.curChar == '`') {
                     this.jjCheckNAddTwoStates(62, 63);
                  }

                  if ((70918500008064L & l) != 0L) {
                     this.jjstateSet[this.jjnewStateCnt++] = 56;
                  } else if ((274877907008L & l) != 0L) {
                     this.jjstateSet[this.jjnewStateCnt++] = 9;
                  } else if ((4503599628419072L & l) != 0L) {
                     this.jjstateSet[this.jjnewStateCnt++] = 5;
                  } else if (this.curChar == '@') {
                     this.jjstateSet[this.jjnewStateCnt++] = 41;
                  }

                  if (this.curChar == '@') {
                     this.jjCheckNAddStates(406, 410);
                  }
                  break;
               case 3:
                  if ((137438953504L & l) != 0L && kind > 543) {
                     kind = 543;
                  }
                  break;
               case 4:
                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAdd(17);
                  }

                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAddTwoStates(16, 17);
                  }

                  if ((9007199256838144L & l) != 0L) {
                     this.jjCheckNAdd(3);
                  }
                  break;
               case 5:
                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAdd(17);
                  }

                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAddTwoStates(16, 17);
                  }

                  if ((1125899907104768L & l) != 0L) {
                     this.jjstateSet[this.jjnewStateCnt++] = 4;
                  }
                  break;
               case 6:
                  if ((4503599628419072L & l) != 0L) {
                     this.jjstateSet[this.jjnewStateCnt++] = 5;
                  }
                  break;
               case 7:
                  if ((2251799814209536L & l) != 0L) {
                     this.jjCheckNAdd(3);
                  }
                  break;
               case 8:
                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAdd(17);
                  }

                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAddTwoStates(16, 17);
                  }

                  if ((17592186048512L & l) != 0L) {
                     this.jjstateSet[this.jjnewStateCnt++] = 7;
                  }
                  break;
               case 9:
                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAdd(17);
                  }

                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAddTwoStates(16, 17);
                  }

                  if ((8589934594L & l) != 0L) {
                     this.jjstateSet[this.jjnewStateCnt++] = 8;
                  }
                  break;
               case 10:
                  if ((274877907008L & l) != 0L) {
                     this.jjstateSet[this.jjnewStateCnt++] = 9;
                  }
               case 11:
               case 12:
               case 13:
               case 14:
               case 15:
               case 18:
               case 20:
               case 24:
               case 25:
               case 26:
               case 30:
               case 31:
               case 32:
               case 33:
               case 34:
               case 37:
               case 38:
               case 39:
               case 40:
               case 42:
               case 43:
               case 44:
               case 45:
               case 46:
               case 50:
               case 52:
               case 53:
               case 58:
               case 59:
               case 69:
               case 72:
               case 73:
               case 74:
               case 76:
               case 77:
               case 78:
               case 79:
               case 80:
               case 81:
               case 82:
               case 83:
               case 84:
               case 85:
               case 86:
               case 88:
               case 91:
               case 92:
               case 93:
               case 95:
               case 97:
               case 100:
               case 101:
               case 103:
               case 106:
               case 107:
               case 108:
               case 110:
               case 112:
               case 115:
               case 116:
               case 117:
               case 119:
               case 122:
               case 123:
               case 127:
               case 128:
               case 129:
               case 130:
               case 131:
               case 132:
               case 133:
               case 134:
               case 135:
               case 136:
               case 137:
               case 138:
               case 140:
               case 142:
               case 143:
               case 144:
               case 146:
               case 149:
               case 150:
               case 151:
               case 152:
               case 153:
               case 156:
               case 157:
               case 158:
               case 159:
               case 160:
               case 161:
               case 164:
               case 165:
               case 166:
               case 169:
               case 170:
               case 171:
               case 172:
               case 173:
               case 176:
               case 177:
               case 178:
               case 179:
               case 180:
               case 182:
               case 183:
               case 184:
               case 185:
               case 186:
               case 187:
               case 188:
               case 189:
               case 190:
               case 191:
               case 192:
               case 193:
               case 194:
               case 195:
               case 196:
               case 197:
               case 198:
               case 199:
               case 200:
               case 201:
               case 202:
               case 203:
               case 204:
               case 205:
               case 208:
               case 211:
               case 212:
               case 215:
               case 216:
               case 217:
               case 218:
               case 219:
               case 221:
               case 222:
               case 223:
               case 224:
               case 225:
               case 226:
               case 227:
               case 228:
               case 229:
               case 230:
               case 231:
               case 232:
               case 233:
               case 234:
               case 235:
               case 236:
               case 237:
               case 238:
               case 239:
               case 240:
               case 241:
               case 242:
               case 243:
               case 244:
               case 245:
               case 247:
               case 248:
               case 249:
               case 250:
               case 251:
               case 252:
               case 253:
               case 254:
               case 255:
               case 256:
               case 257:
               case 258:
               case 259:
               case 260:
               case 261:
               case 262:
               case 263:
               case 264:
               case 265:
               case 266:
               case 267:
               case 268:
               case 269:
               case 270:
               case 271:
               case 273:
               case 274:
               case 275:
               case 276:
               case 277:
               case 278:
               case 279:
               case 280:
               case 281:
               case 282:
               case 283:
               case 284:
               case 285:
               case 286:
               case 287:
               case 288:
               case 289:
               case 290:
               case 291:
               case 292:
               case 293:
               case 294:
               case 299:
               default:
                  break;
               case 16:
                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAddTwoStates(16, 17);
                  }
                  break;
               case 17:
                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAdd(17);
                  }
                  break;
               case 19:
                  this.jjAddStates(415, 416);
                  break;
               case 21:
                  if (this.curChar == '@') {
                     this.jjCheckNAddStates(406, 410);
                  }
                  break;
               case 22:
                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 549) {
                        kind = 549;
                     }

                     this.jjCheckNAddStates(64, 66);
                  } else if (this.curChar == '@') {
                     this.jjCheckNAddStates(411, 414);
                  }

                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 548) {
                        kind = 548;
                     }

                     this.jjCheckNAddStates(11, 13);
                  }
                  break;
               case 23:
                  this.jjCheckNAddTwoStates(23, 24);
                  break;
               case 27:
                  this.jjCheckNAddTwoStates(27, 24);
                  break;
               case 28:
                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 549) {
                        kind = 549;
                     }

                     this.jjCheckNAddStates(72, 74);
                  }
                  break;
               case 29:
                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 549) {
                        kind = 549;
                     }

                     this.jjCheckNAddTwoStates(25, 29);
                  }
                  break;
               case 35:
                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 549) {
                        kind = 549;
                     }

                     this.jjCheckNAddStates(64, 66);
                  }
                  break;
               case 36:
                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 549) {
                        kind = 549;
                     }

                     this.jjCheckNAddTwoStates(36, 25);
                  }
                  break;
               case 41:
                  if (this.curChar == '@') {
                     this.jjCheckNAddStates(411, 414);
                  }
                  break;
               case 47:
                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 552) {
                        kind = 552;
                     }

                     this.jjCheckNAddTwoStates(47, 48);
                  }
                  break;
               case 48:
                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 552) {
                        kind = 552;
                     }

                     this.jjCheckNAdd(48);
                  }
                  break;
               case 49:
                  if (this.curChar == '@') {
                     this.jjstateSet[this.jjnewStateCnt++] = 41;
                  }
                  break;
               case 51:
                  this.jjCheckNAddTwoStates(51, 52);
                  break;
               case 54:
                  this.jjCheckNAddTwoStates(54, 52);
                  break;
               case 55:
                  if ((70918500008064L & l) != 0L) {
                     this.jjstateSet[this.jjnewStateCnt++] = 56;
                  }
                  break;
               case 56:
                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAdd(17);
                  }

                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAddTwoStates(16, 17);
                  }
                  break;
               case 57:
                  this.jjCheckNAddTwoStates(57, 58);
                  break;
               case 60:
                  this.jjCheckNAddTwoStates(60, 58);
                  break;
               case 61:
                  if (this.curChar == '`') {
                     this.jjCheckNAddTwoStates(62, 63);
                  }
                  break;
               case 62:
                  if ((-4294967297L & l) != 0L) {
                     this.jjCheckNAddTwoStates(62, 63);
                  }
                  break;
               case 63:
                  if (this.curChar == '`') {
                     if (kind > 554) {
                        kind = 554;
                     }

                     this.jjstateSet[this.jjnewStateCnt++] = 64;
                  }
                  break;
               case 64:
                  if (this.curChar == '`') {
                     this.jjCheckNAddTwoStates(65, 63);
                  }
                  break;
               case 65:
                  if ((-4294967297L & l) != 0L) {
                     this.jjCheckNAddTwoStates(65, 63);
                  }
                  break;
               case 66:
                  if (this.curChar == '[') {
                     this.jjCheckNAddTwoStates(67, 68);
                  }
                  break;
               case 67:
                  if ((-536870913L & l) != 0L) {
                     this.jjCheckNAddTwoStates(67, 68);
                  }
                  break;
               case 68:
                  if (this.curChar == ']' && kind > 555) {
                     kind = 555;
                  }
                  break;
               case 70:
                  if ((72057594054705152L & l) != 0L) {
                     if (kind > 558) {
                        kind = 558;
                     }

                     this.jjCheckNAdd(71);
                  }
                  break;
               case 71:
                  if ((541165879422L & l) != 0L) {
                     if (kind > 558) {
                        kind = 558;
                     }

                     this.jjCheckNAdd(71);
                  }
                  break;
               case 75:
                  if ((139586437152L & l) != 0L) {
                     this.jjAddStates(417, 419);
                  }
                  break;
               case 87:
                  this.jjCheckNAddTwoStates(87, 88);
                  break;
               case 89:
               case 90:
                  this.jjCheckNAddTwoStates(90, 88);
                  break;
               case 94:
                  if ((576460745995190270L & l) != 0L) {
                     this.jjAddStates(420, 421);
                  }
                  break;
               case 96:
                  this.jjCheckNAddTwoStates(96, 97);
                  break;
               case 98:
               case 99:
                  this.jjCheckNAddTwoStates(99, 97);
                  break;
               case 102:
                  this.jjCheckNAddTwoStates(102, 103);
                  break;
               case 104:
               case 105:
                  this.jjCheckNAddTwoStates(105, 103);
                  break;
               case 109:
                  if ((576460745995190270L & l) != 0L) {
                     this.jjAddStates(422, 423);
                  }
                  break;
               case 111:
                  this.jjCheckNAddTwoStates(111, 112);
                  break;
               case 113:
               case 114:
                  this.jjCheckNAddTwoStates(114, 112);
                  break;
               case 118:
                  this.jjCheckNAddTwoStates(118, 119);
                  break;
               case 120:
               case 121:
                  this.jjCheckNAddTwoStates(121, 119);
                  break;
               case 124:
                  if (this.curChar == '{') {
                     this.jjCheckNAddTwoStates(125, 126);
                  }
                  break;
               case 125:
                  if ((-2305843009213693953L & l) != 0L) {
                     this.jjCheckNAddTwoStates(125, 126);
                  }
                  break;
               case 126:
                  if (this.curChar == '}' && kind > 546) {
                     kind = 546;
                  }
                  break;
               case 139:
                  if (this.curChar == '@') {
                     this.jjCheckNAddStates(41, 53);
                  }
                  break;
               case 141:
                  this.jjAddStates(424, 425);
                  break;
               case 145:
                  this.jjAddStates(426, 427);
                  break;
               case 147:
                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 548) {
                        kind = 548;
                     }

                     this.jjCheckNAddTwoStates(147, 148);
                  }
                  break;
               case 148:
                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 548) {
                        kind = 548;
                     }

                     this.jjCheckNAdd(148);
                  }
                  break;
               case 154:
               case 302:
                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 548) {
                        kind = 548;
                     }

                     this.jjCheckNAddStates(11, 13);
                  }
                  break;
               case 155:
                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 548) {
                        kind = 548;
                     }

                     this.jjCheckNAddTwoStates(155, 143);
                  }
                  break;
               case 162:
                  if ((72057594054705152L & l) != 0L) {
                     this.jjCheckNAddStates(112, 116);
                  }
                  break;
               case 163:
                  if ((541165879422L & l) != 0L) {
                     this.jjCheckNAddStates(112, 116);
                  }
                  break;
               case 167:
                  if ((72057594054705152L & l) != 0L) {
                     this.jjCheckNAddStates(121, 125);
                  }
                  break;
               case 168:
                  if ((541165879422L & l) != 0L) {
                     this.jjCheckNAddStates(121, 125);
                  }
                  break;
               case 174:
                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 550) {
                        kind = 550;
                     }

                     this.jjCheckNAddTwoStates(174, 175);
                  }
                  break;
               case 175:
                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 550) {
                        kind = 550;
                     }

                     this.jjCheckNAdd(175);
                  }
                  break;
               case 181:
                  if ((139586437152L & l) != 0L) {
                     this.jjAddStates(428, 430);
                  }
                  break;
               case 206:
                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 550) {
                        kind = 550;
                     }

                     this.jjCheckNAddStates(232, 234);
                  }
                  break;
               case 207:
                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 550) {
                        kind = 550;
                     }

                     this.jjCheckNAddTwoStates(207, 165);
                  }
                  break;
               case 209:
                  if ((72057594054705152L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddTwoStates(210, 211);
                  }
                  break;
               case 210:
                  if ((541165879422L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAddTwoStates(210, 211);
                  }
                  break;
               case 213:
                  if ((72057594054705152L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAdd(214);
                  }
                  break;
               case 214:
                  if ((541165879422L & l) != 0L) {
                     if (kind > 551) {
                        kind = 551;
                     }

                     this.jjCheckNAdd(214);
                  }
                  break;
               case 220:
                  if ((139586437152L & l) != 0L) {
                     this.jjAddStates(431, 433);
                  }
                  break;
               case 246:
                  if ((139586437152L & l) != 0L) {
                     this.jjAddStates(434, 436);
                  }
                  break;
               case 272:
                  if ((139586437152L & l) != 0L) {
                     this.jjAddStates(437, 439);
                  }
                  break;
               case 295:
                  if ((17179869188L & l) != 0L && kind > 559) {
                     kind = 559;
                  }
                  break;
               case 296:
                  if ((35184372097024L & l) != 0L) {
                     this.jjCheckNAdd(295);
                  }
                  break;
               case 297:
                  if ((8796093024256L & l) != 0L) {
                     this.jjCheckNAdd(295);
                  }
                  break;
               case 298:
                  if ((44530220935296L & l) != 0L && kind > 559) {
                     kind = 559;
                  }
                  break;
               case 300:
                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAdd(17);
                  }

                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAddTwoStates(16, 17);
                  }
                  break;
               case 301:
                  if ((-536870913L & l) != 0L) {
                     this.jjCheckNAddTwoStates(67, 68);
                  } else if (this.curChar == ']' && kind > 555) {
                     kind = 555;
                  }
                  break;
               case 303:
                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAdd(17);
                  }

                  if ((576460745995190270L & l) != 0L) {
                     if (kind > 544) {
                        kind = 544;
                     }

                     this.jjCheckNAddTwoStates(16, 17);
                  }

                  if ((137438953504L & l) != 0L && kind > 543) {
                     kind = 543;
                  }
               }
            } while(i != startsAt);
         } else {
            int hiByte = this.curChar >> 8;
            int i1 = hiByte >> 6;
            long l1 = 1L << (hiByte & 63);
            int i2 = (this.curChar & 255) >> 6;
            long l2 = 1L << (this.curChar & 63);

            do {
               --i;
               switch(this.jjstateSet[i]) {
               case 1:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                     if (kind > 5) {
                        kind = 5;
                     }

                     this.jjstateSet[this.jjnewStateCnt++] = 1;
                  }
                  break;
               case 19:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                     this.jjAddStates(415, 416);
                  }
                  break;
               case 23:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                     this.jjCheckNAddTwoStates(23, 24);
                  }
                  break;
               case 27:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                     this.jjCheckNAddTwoStates(27, 24);
                  }
                  break;
               case 51:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                     this.jjCheckNAddTwoStates(51, 52);
                  }
                  break;
               case 54:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                     this.jjCheckNAddTwoStates(54, 52);
                  }
                  break;
               case 57:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                     this.jjCheckNAddTwoStates(57, 58);
                  }
                  break;
               case 60:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                     this.jjCheckNAddTwoStates(60, 58);
                  }
                  break;
               case 62:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                     this.jjCheckNAddTwoStates(62, 63);
                  }
                  break;
               case 65:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                     this.jjCheckNAddTwoStates(65, 63);
                  }
                  break;
               case 67:
               case 301:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                     this.jjCheckNAddTwoStates(67, 68);
                  }
                  break;
               case 87:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                     this.jjCheckNAddTwoStates(87, 88);
                  }
                  break;
               case 89:
               case 90:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                     this.jjCheckNAddTwoStates(90, 88);
                  }
                  break;
               case 96:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                     this.jjCheckNAddTwoStates(96, 97);
                  }
                  break;
               case 98:
               case 99:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                     this.jjCheckNAddTwoStates(99, 97);
                  }
                  break;
               case 102:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                     this.jjCheckNAddTwoStates(102, 103);
                  }
                  break;
               case 104:
               case 105:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                     this.jjCheckNAddTwoStates(105, 103);
                  }
                  break;
               case 111:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                     this.jjCheckNAddTwoStates(111, 112);
                  }
                  break;
               case 113:
               case 114:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                     this.jjCheckNAddTwoStates(114, 112);
                  }
                  break;
               case 118:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                     this.jjCheckNAddTwoStates(118, 119);
                  }
                  break;
               case 120:
               case 121:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                     this.jjCheckNAddTwoStates(121, 119);
                  }
                  break;
               case 125:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                     this.jjAddStates(95, 96);
                  }
                  break;
               case 141:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                     this.jjAddStates(424, 425);
                  }
                  break;
               case 145:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                     this.jjAddStates(426, 427);
                  }
               }
            } while(i != startsAt);
         }

         if (kind != Integer.MAX_VALUE) {
            this.jjmatchedKind = kind;
            this.jjmatchedPos = curPos;
            kind = Integer.MAX_VALUE;
         }

         ++curPos;
         if ((i = this.jjnewStateCnt) == (startsAt = 299 - (this.jjnewStateCnt = startsAt))) {
            return curPos;
         }

         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var13) {
            return curPos;
         }
      }
   }

   private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2) {
      switch(hiByte) {
      case 0:
         return (jjbitVec2[i2] & l2) != 0L;
      default:
         return (jjbitVec0[i1] & l1) != 0L;
      }
   }

   public ALLSQLTokenManager(JavaCharStream stream) {
      this.debugStream = System.out;
      this.jjrounds = new int[299];
      this.jjstateSet = new int[598];
      this.curLexState = 0;
      this.defaultLexState = 0;
      this.input_stream = stream;
   }

   public ALLSQLTokenManager(JavaCharStream stream, int lexState) {
      this(stream);
      this.SwitchTo(lexState);
   }

   public void ReInit(JavaCharStream stream) {
      this.jjmatchedPos = this.jjnewStateCnt = 0;
      this.curLexState = this.defaultLexState;
      this.input_stream = stream;
      this.ReInitRounds();
   }

   private void ReInitRounds() {
      this.jjround = -2147483647;

      for(int i = 299; i-- > 0; this.jjrounds[i] = Integer.MIN_VALUE) {
      }

   }

   public void ReInit(JavaCharStream stream, int lexState) {
      this.ReInit(stream);
      this.SwitchTo(lexState);
   }

   public void SwitchTo(int lexState) {
      if (lexState < 1 && lexState >= 0) {
         this.curLexState = lexState;
      } else {
         throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
      }
   }

   protected Token jjFillToken() {
      String im = jjstrLiteralImages[this.jjmatchedKind];
      String curTokenImage = im == null ? this.input_stream.GetImage() : im;
      int beginLine = this.input_stream.getBeginLine();
      int beginColumn = this.input_stream.getBeginColumn();
      int endLine = this.input_stream.getEndLine();
      int endColumn = this.input_stream.getEndColumn();
      Token t = Token.newToken(this.jjmatchedKind, curTokenImage);
      t.beginLine = beginLine;
      t.endLine = endLine;
      t.beginColumn = beginColumn;
      t.endColumn = endColumn;
      return t;
   }

   public Token getNextToken() {
      Token specialToken = null;
      boolean var3 = false;

      while(true) {
         Token matchedToken;
         label76:
         while(true) {
            try {
               this.curChar = this.input_stream.BeginToken();
            } catch (IOException var9) {
               this.jjmatchedKind = 0;
               matchedToken = this.jjFillToken();
               matchedToken.specialToken = specialToken;
               return matchedToken;
            }

            try {
               this.input_stream.backup(0);

               while(true) {
                  if (this.curChar > ' ' || (4294977024L & 1L << this.curChar) == 0L) {
                     break label76;
                  }

                  this.curChar = this.input_stream.BeginToken();
               }
            } catch (IOException var11) {
            }
         }

         this.jjmatchedKind = Integer.MAX_VALUE;
         this.jjmatchedPos = 0;
         int curPos = this.jjMoveStringLiteralDfa0_0();
         if (this.jjmatchedKind == Integer.MAX_VALUE) {
            int error_line = this.input_stream.getEndLine();
            int error_column = this.input_stream.getEndColumn();
            String error_after = null;
            boolean EOFSeen = false;

            try {
               this.input_stream.readChar();
               this.input_stream.backup(1);
            } catch (IOException var10) {
               EOFSeen = true;
               error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
               if (this.curChar != '\n' && this.curChar != '\r') {
                  ++error_column;
               } else {
                  ++error_line;
                  error_column = 0;
               }
            }

            if (!EOFSeen) {
               this.input_stream.backup(1);
               error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
            }

            throw new TokenMgrError(EOFSeen, this.curLexState, error_line, error_column, error_after, this.curChar, 0);
         }

         if (this.jjmatchedPos + 1 < curPos) {
            this.input_stream.backup(curPos - this.jjmatchedPos - 1);
         }

         if ((jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 63)) != 0L) {
            matchedToken = this.jjFillToken();
            matchedToken.specialToken = specialToken;
            return matchedToken;
         }

         if ((jjtoSpecial[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 63)) != 0L) {
            matchedToken = this.jjFillToken();
            if (specialToken == null) {
               specialToken = matchedToken;
            } else {
               matchedToken.specialToken = specialToken;
               specialToken = specialToken.next = matchedToken;
            }
         }
      }
   }

   private void jjCheckNAdd(int state) {
      if (this.jjrounds[state] != this.jjround) {
         this.jjstateSet[this.jjnewStateCnt++] = state;
         this.jjrounds[state] = this.jjround;
      }

   }

   private void jjAddStates(int start, int end) {
      do {
         this.jjstateSet[this.jjnewStateCnt++] = jjnextStates[start];
      } while(start++ != end);

   }

   private void jjCheckNAddTwoStates(int state1, int state2) {
      this.jjCheckNAdd(state1);
      this.jjCheckNAdd(state2);
   }

   private void jjCheckNAddStates(int start, int end) {
      do {
         this.jjCheckNAdd(jjnextStates[start]);
      } while(start++ != end);

   }
}
