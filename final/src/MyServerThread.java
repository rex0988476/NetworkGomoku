import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class MyServerThread extends Thread {
	public Socket s1;
	public Socket s2;
	public Socket socket1;
	public Socket socket2;
	public static int id=0;
	public int id_ = 0;
	public boolean isExit=false;
	char[][] table = new char[15][15];
	InputStream in1;
	OutputStream out1;
	InputStream in2;
	OutputStream out2;
	public MyServerThread(Socket s1_, Socket s2_) {
		super();
		socket1 = s1_;
		socket2 = s2_;
		try {
			in1 = socket1.getInputStream();
			out1 = socket1.getOutputStream();
			in2 = socket2.getInputStream();
			out2 = socket2.getOutputStream();
		}catch (Exception e){
			e.printStackTrace();
		}

		id++;
		id_ = id;
	}
	
	public void run() {
		try {
			System.out.println("("+id_+") A new table is created");
			System.out.println("thread id: " + id_);
			byte[] oneBuffer = new byte[1];
			byte[] typeBuffer = new byte[4];
			byte[] iBuffer = new byte[2];
			byte[] jBuffer = new byte[2];
			byte[] buffer;
			int turn=0;
			int indexI,indexJ,lenI,lenJ;
			String sBuffer;
			char player1Sign,player2Sign;
			while (true) {
				init();
				System.out.println("init");
				while (true) {
					System.out.println("start play");
					sleep(100);
					//turn
					int r = 0;
					r = (int) (Math.random() * 2);
					if (r == 0) {
						System.out.println("if");
						turn=0;
						out1.write("0".getBytes());
						out1.flush();
						out2.write("1".getBytes());
						out2.flush();
						player1Sign='B';
						player2Sign='W';
					} else {
						System.out.println("else");
						turn=1;
						out1.write("1".getBytes());
						out1.flush();
						out2.write("0".getBytes());
						out2.flush();
						player1Sign='W';
						player2Sign='B';
					}
					System.out.println("turn fin, start play");

					//play
					try {
						while (true) {
							if (turn == 0) {
								System.out.println("(" + id_ + ") client1's turn. waiting client1 reply...");
								//recv
								typeBuffer = new byte[4];
								in1.read(typeBuffer);
								sBuffer = new String(typeBuffer);

								if (sBuffer.equals("tie_")) {
									out2.write("ctie".getBytes());
									out2.flush();
									typeBuffer = new byte[4];
									in2.read(typeBuffer);
									sBuffer = new String(typeBuffer);
									if (sBuffer.equals("yes_")) {
										System.out.println("tie");
										out1.write("TIE_".getBytes());
										out1.flush();
										out2.write("TIE_".getBytes());
										out2.flush();
										break;
									}
									if (sBuffer.equals("no__")) {
										typeBuffer = new byte[4];
										out1.write("no__".getBytes());
										out1.flush();
										in1.read(typeBuffer);
										sBuffer = new String(typeBuffer);
									}
								}
								if (sBuffer.equals("surr")) {
									out1.write("SURR".getBytes());
									out1.flush();
									out2.write("surr".getBytes());
									out2.flush();
									break;
								}
								if (sBuffer.equals("play")) {
									//i
									in1.read(oneBuffer);
									sBuffer = new String(oneBuffer);
									lenI = Integer.parseInt(sBuffer);
									if (lenI == 1) {
										iBuffer = new byte[1];
									} else if (lenI == 2) {
										iBuffer = new byte[2];
									}
									in1.read(iBuffer);
									sBuffer = new String(iBuffer);
									indexI = Integer.parseInt(sBuffer);

									//j
									in1.read(oneBuffer);
									sBuffer = new String(oneBuffer);
									lenJ = Integer.parseInt(sBuffer);
									if (lenJ == 1) {
										jBuffer = new byte[1];
									} else if (lenJ == 2) {
										jBuffer = new byte[2];
									}
									in1.read(jBuffer);
									sBuffer = new String(jBuffer);
									indexJ = Integer.parseInt(sBuffer);

									table[indexI][indexJ] = player1Sign;

									//send
									System.out.println("play2");
									out2.write("play".getBytes());
									out2.flush();
									sleep(100);
									out2.write(Integer.toString(lenI).getBytes());
									out2.flush();
									sleep(100);
									out2.write(Integer.toString(indexI).getBytes());
									out2.flush();
									sleep(100);
									out2.write(Integer.toString(lenJ).getBytes());
									out2.flush();
									sleep(100);
									out2.write(Integer.toString(indexJ).getBytes());
									out2.flush();
								}
							}

							///////////////////////////////////////////////////////////////////
							if (turn == 1) {
								System.out.println("(" + id_ + ") client2's turn. waiting client2 reply...");
								//recv
								typeBuffer = new byte[4];
								in2.read(typeBuffer);
								sBuffer = new String(typeBuffer);

								if (sBuffer.equals("tie_")) {
									out1.write("ctie".getBytes());
									out1.flush();
									typeBuffer = new byte[4];
									in1.read(typeBuffer);
									sBuffer = new String(typeBuffer);
									if (sBuffer.equals("yes_")) {
										System.out.println("tie");
										out1.write("TIE_".getBytes());
										out1.flush();
										out2.write("TIE_".getBytes());
										out2.flush();
										break;
									}
									if (sBuffer.equals("no__")) {
										typeBuffer = new byte[4];
										out2.write("no__".getBytes());
										out2.flush();
										in2.read(typeBuffer);
										sBuffer = new String(typeBuffer);
									}
								}
								if (sBuffer.equals("surr")) {
									out2.write("SURR".getBytes());
									out2.flush();
									out1.write("surr".getBytes());
									out1.flush();
									break;
								}
								if (sBuffer.equals("play")) {
									//i
									in2.read(oneBuffer);
									sBuffer = new String(oneBuffer);
									lenI = Integer.parseInt(sBuffer);
									if (lenI == 1) {
										iBuffer = new byte[1];
									} else if (lenI == 2) {
										iBuffer = new byte[2];
									}
									in2.read(iBuffer);
									sBuffer = new String(iBuffer);
									indexI = Integer.parseInt(sBuffer);

									//j
									in2.read(oneBuffer);
									sBuffer = new String(oneBuffer);
									lenJ = Integer.parseInt(sBuffer);
									if (lenJ == 1) {
										jBuffer = new byte[1];
									} else if (lenJ == 2) {
										jBuffer = new byte[2];
									}
									in2.read(jBuffer);
									sBuffer = new String(jBuffer);
									indexJ = Integer.parseInt(sBuffer);

									table[indexI][indexJ] = player2Sign;

									//send
									System.out.println("play1");
									out1.write("play".getBytes());
									out1.flush();
									sleep(100);
									out1.write(Integer.toString(lenI).getBytes());
									out1.flush();
									sleep(100);
									out1.write(Integer.toString(indexI).getBytes());
									out1.flush();
									sleep(100);
									out1.write(Integer.toString(lenJ).getBytes());
									out1.flush();
									sleep(100);
									out1.write(Integer.toString(indexJ).getBytes());
									out1.flush();
								}
							}
							//isWin
							sleep(100);
							if (isWin() == 0) {
								System.out.println("black win");
								out1.write("YWIN".getBytes());
								out1.flush();
								out2.write("OWIN".getBytes());
								out2.flush();
								break;
							} else if (isWin() == 1) {
								System.out.println("white win");
								out1.write("OWIN".getBytes());
								out1.flush();
								out2.write("YWIN".getBytes());
								out2.flush();
								break;
							} else if (isWin() == 2) {
								System.out.println("tie");
								out1.write("TIE_".getBytes());
								out1.flush();
								out2.write("TIE_".getBytes());
								out2.flush();
								break;
							}
							//turn
							turn = (turn + 1) % 2;
							//special in
							System.out.println("special in");
							if (turn == 1) {
								if (isLiveThree(player1Sign) == 2) {
									System.out.println("1 DLT");
									out2.write("DLT__".getBytes());
									out2.flush();
								} else if (isLiveThree(player1Sign) == 1) {
									System.out.println("1 LT");
									out2.write("LT__".getBytes());
									out2.flush();
								}
								if (isDeadFour(player1Sign) == 1) {
									System.out.println("1 DF");
									out2.write("DF__".getBytes());
									out2.flush();
								}
							}
							if (turn == 0) {
								if (isLiveThree(player2Sign) == 2) {
									System.out.println("2 DLT");
									out1.write("DLT__".getBytes());
									out1.flush();
								} else if (isLiveThree(player2Sign) == 1) {
									System.out.println("2 LT");
									out1.write("LT__".getBytes());
									out1.flush();
								}
								if (isDeadFour(player2Sign) == 1) {
									System.out.println("2 DF");
									out1.write("DF__".getBytes());
									out1.flush();
								}
							}
						}
					}catch (Exception e){
						System.out.println("playing error");
						try {
							out1.write("plaE".getBytes());
							out1.flush();
						}catch (Exception ee){
							ee.printStackTrace();
						}
						try {
							out2.write("plaE".getBytes());
							out2.flush();
						}catch (Exception ee){
							ee.printStackTrace();
						}
					}

					int tt1state = 0, tt2state = 0;
					MyServerEndGameThread tt1 = new MyServerEndGameThread(in1);
					MyServerEndGameThread tt2 = new MyServerEndGameThread(in2);
					tt1.start();
					tt2.start();
					int first1 = 1, first2 = 1, first1Leave = 1, first2Leave = 1, first1error = 1, first2error = 1;
					while(!tt1.canGet() || !tt2.canGet()){
						System.out.println("tt1");
						System.out.println(tt1.isGet);
						System.out.println();
						System.out.println("tt2");
						System.out.println(tt2.isGet);
						if(tt1.canGet()){
							System.out.println("tt1get");
							tt1state=tt1.get_state();
							break;
						}
						if(tt2.canGet()){
							System.out.println("tt2get");
							tt2state=tt2.get_state();
							break;
						}
					}
					if(tt1state!=0){
						if(tt1state == 1 && first1 == 1){//reset1
							first1 = 0;
							System.out.println("221");
							out1.write("RESET".getBytes());
							out1.flush();
							//if(first2==1){
							System.out.println("225");
								out2.write("reset".getBytes());
								out2.flush();
							//}
							System.out.println("reset 1");
						}
						if (tt1state == -1 && first1 == 1) {//leave1
							System.out.println("232leave");
							first1 = 0;
							first1Leave = 0;
							out1.write("LEAVE".getBytes());
							out1.flush();
							sleep(100);
							if(first2Leave==1) {
								out2.write("leave".getBytes());
								out2.flush();
							}
							sleep(1000);
							System.out.println("close 1");
							in1.close();
							out1.close();
							socket1.close();
						}
						if(tt1state == -2 && first1error == 1){//connect error
							first1error = 0;
							System.out.println("1 connect error");
							out2.write("endE_".getBytes());
							out2.flush();
						}
					}
					System.out.println("=245");
					if(tt2state!=0){
						if(tt2state == 1 && first2 == 1){//reset2
							first2 = 0;
							System.out.println("255");
							out2.write("RESET".getBytes());
							out2.flush();
							//if(first1==1) {
							System.out.println("259");
							out1.write("reset".getBytes());
							out1.flush();
							//}
							System.out.println("reset 2");
						}
						if (tt2state == -1 && first2 == 1) {//leave2
							System.out.println("272leave");
							first2 = 0;
							first2Leave = 0;
							out2.write("LEAVE".getBytes());
							out2.flush();
							sleep(100);
							if(first1Leave==1) {
								out1.write("leave".getBytes());
								out1.flush();
							}
							System.out.println("close 2");
							in2.close();
							out2.close();
							socket2.close();
						}
						if(tt2state == -2 && first2error == 1){//connect error
							first2error = 0;
							System.out.println("2 connect error");
							out1.write("endE_".getBytes());
							out1.flush();
						}
					}
					System.out.println("=253");
					while (tt1state==0){
						System.out.println(tt1.isGet);
						//sleep(10);
						if(tt1.canGet()){
							System.out.println("tt1get");
							tt1state=tt1.get_state();
							break;
						}
					}
					System.out.println("=281");
					while (tt2state==0){
						System.out.println(tt2.isGet);
						//sleep(10);
						if(tt2.canGet()){
							System.out.println("tt2get");
							tt2state=tt2.get_state();
							break;
						}
					}
					System.out.println("=289");
					if(tt1state!=0){
						if(tt1state == 1 && first1 == 1){//reset1
							first1 = 0;
							System.out.println("289");
							out1.write("RESET".getBytes());
							out1.flush();
							//if(first2==1){
							System.out.println("293");
								out2.write("reset".getBytes());
								out2.flush();
							//}
							System.out.println("reset 1");
						}
						if (tt1state == -1 && first1 == 1) {//leave1
							System.out.println("311leave");
							first1 = 0;
							first1Leave = 0;
							out1.write("LEAVE".getBytes());
							out1.flush();
							sleep(100);
							if(first2Leave==1) {
								out2.write("leave".getBytes());
								out2.flush();
							}
							System.out.println("close 1");
							in1.close();
							out1.close();
							socket1.close();
						}
						if(tt1state == -2 && first1error == 1){//connect error
							first1error = 0;
							System.out.println("1 connect error");
							out2.write("endE_".getBytes());
							out2.flush();
						}
					}
					if(tt2state!=0){
						if(tt2state == 1 && first2 == 1){//reset2
							first2 = 0;
							System.out.println("316");
							out2.write("RESET".getBytes());
							out2.flush();
							//if(first1==1) {
							System.out.println("320");
								out1.write("reset".getBytes());
								out1.flush();
							//}
							System.out.println("reset 2");
						}
						if (tt2state == -1 && first2 == 1) {//leave2
							System.out.println("339leave");
							first2 = 0;
							first2Leave = 0;
							out2.write("LEAVE".getBytes());
							out2.flush();
							sleep(100);
							if(first1Leave==1) {
								out1.write("leave".getBytes());
								out1.flush();
							}
							System.out.println("close 2");
							in2.close();
							out2.close();
							socket2.close();
						}
						if(tt2state == -2 && first2error == 1){//connect error
							first2error = 0;
							System.out.println("2 connect error");
							out1.write("endE_".getBytes());
							out1.flush();
						}
					}
					System.out.println("wait end game fin");
					System.out.println(tt1state);
					System.out.println(tt2state);
					if (tt1state == -1 || tt2state == -1 || tt1state == -2 || tt2state == -2) {
						isExit=true;
						//System.exit(1);
					}
					break;
				}
				if(isExit){
					break;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("("+id_+") play thread fin.");
	}

	public void init(){
		for (int i=0;i<15;i++){
			for(int j=0;j<15;j++){
				table[i][j] = ' ';
			}
		}
	}

	public int isWin(){
		boolean isSame=true;
		boolean isTie=true;
		for(int i=0;i<15;i++){
			for(int j=0;j<15;j++){
				//isTie
				if(table[i][j]== ' '){
					isTie=false;
				}
				//go_down
				isSame=true;
				if(i<=10){
					for(int ii=i;ii-i<4;ii++){
						if(table[ii][j]!= ' ' && table[ii][j]!=table[ii+1][j]){
							isSame=false;
							break;
						}
					}
					if(isSame){
						if(table[i][j]=='B'){
							return 0;
						}
						else if(table[i][j]=='W'){
							return 1;
						}
					}
				}
				//go_up
				isSame=true;
				if(i>=5){
					for(int ii=i;i-ii<4;ii--){
						if(table[ii][j]!= ' ' && table[ii][j]!=table[ii-1][j]){
							isSame=false;
							break;
						}
					}
					if(isSame){
						if(table[i][j]=='B'){
							return 0;
						}
						else if(table[i][j]=='W'){
							return 1;
						}
					}
				}
				//go_right
				isSame=true;
				if(j<=10){
					for(int jj=j;jj-j<4;jj++){
						if(table[i][jj]!= ' ' && table[i][jj]!=table[i][jj+1]){
							isSame=false;
							break;
						}
					}
					if(isSame){
						if(table[i][j]=='B'){
							return 0;
						}
						else if(table[i][j]=='W'){
							return 1;
						}
					}
				}
				//go_left
				isSame=true;
				if(j>=5){
					for(int jj=j;j-jj<4;jj--){
						if(table[i][jj]!= ' ' && table[i][jj]!=table[i][jj-1]){
							isSame=false;
							break;
						}
					}
					if(isSame){
						if(table[i][j]=='B'){
							return 0;
						}
						else if(table[i][j]=='W'){
							return 1;
						}
					}
				}
				//go_left_up
				isSame=true;
				if(j>=5 && i>=5){
					int jj=j,ii=i;
					while (j-jj<4 && i-ii<4){
						if(table[ii][jj]!= ' ' && table[ii][jj]!=table[ii-1][jj-1]){
							isSame=false;
							break;
						}
						jj--;
						ii--;
					}
					if(isSame){
						if(table[i][j]=='B'){
							return 0;
						}
						else if(table[i][j]=='W'){
							return 1;
						}
					}
				}
				//go_left_down
				isSame=true;
				if(j>=5 && i<=10){
					int jj=j,ii=i;
					while (j-jj<4 && ii-i<4){
						if(table[ii][jj]!= ' ' && table[ii][jj]!=table[ii+1][jj-1]){
							isSame=false;
							break;
						}
						jj--;
						ii++;
					}
					if(isSame){
						if(table[i][j]=='B'){
							return 0;
						}
						else if(table[i][j]=='W'){
							return 1;
						}
					}
				}
				//go_right_up
				isSame=true;
				if(j<=10 && i>=5){
					int jj=j,ii=i;
					while (jj-j<4 && i-ii<4){
						if(table[ii][jj]!= ' ' && table[ii][jj]!=table[ii-1][jj+1]){
							isSame=false;
							break;
						}
						jj++;
						ii--;
					}
					if(isSame){
						if(table[i][j]=='B'){
							return 0;
						}
						else if(table[i][j]=='W'){
							return 1;
						}
					}
				}
				//go_right_down
				isSame=true;
				if(j<=10 && i<=10){
					int jj=j,ii=i;
					while (jj-j<4 && ii-i<4){
						if(table[ii][jj]!= ' ' && table[ii][jj]!=table[ii+1][jj+1]){
							isSame=false;
							break;
						}
						jj++;
						ii++;
					}
					if(isSame){
						if(table[i][j]=='B'){
							return 0;
						}
						else if(table[i][j]=='W'){
							return 1;
						}
					}
				}

			}
		}

		if(isTie){
			return 2;
		}
		return -1;
	}

	public int isLiveFour(){
		int islivefour=0;
		for(int i=1;i<12;i++){
			for(int j=1;j<12;j++){
				if(table[i][j]!=' '){
					if(i>1 && i<10){
						if(table[i-2][j]!=table[i][j] && table[i-1][j]==' ' && table[i+1][j]==table[i][j] && table[i+2][j]==table[i][j] && table[i+3][j]==table[i][j] && table[i+4][j]==' ' && table[i+5][j]!=table[i][j]){
							;
						}
					}
				}
			}
		}
		return islivefour;
	}

	public int isDeadFour(char sign){
		int isdeadfour=0;
		int count=0,space=0;
		for(int i=0;i<15;i++){
			for(int j=0;j<15;j++){
				//up & down
				count=0;
				space=0;
				for(int k=i;k-i<5 && i<11 && space<2;k++){
					if(table[k][j]==sign){
						count++;
					}
					else if(table[k][j]==' '){
						space++;
					}
					else {
						break;
					}
				}
				//jump
				if(count==4 && space ==1){
					return 1;
				}
				//left & right
				count=0;
				space=0;
				for (int k = j; k - j < 5 && j < 11 && space<2; k++) {
					if (table[i][k]==sign){
						count++;
					}
					else if(table[i][k]==' '){
						space++;
					}
					else {
						break;
					}
				}
				if(count==4 && space==1){
					return 1;
					//isdeadfour=1;
				}
				//left up & right down
				count=0;
				space=0;
				int k=i,l=j;
				while (l-j<5 && j<11 && k-i<5 && i<11 && space<2){
					if (table[k][l]==sign){
						count++;
					}
					else if(table[k][l]==' '){
						space++;
					}
					else {
						break;
					}
					l++;
					k++;
				}
				if(count==4 && space==1){
					return 1;
					//isdeadfour=1;
				}
				//right up & left down
				count=0;
				space=0;
				k=i;l=j;
				while(j>3 && i<11 && k-i<5 && j-l<5 && space<2){
					if (table[k][l]==sign){
						count++;
					}
					else if(table[k][l]==' '){
						space++;
					}
					else {
						break;
					}
					l--;
					k++;
				}
				if(count==4 && space==1){
					return 1;
					//isdeadfour=1;
				}
			}
		}
		return isdeadfour;
	}

	public int isLiveThree(char sign){
		int livethreecount=0,saveNum=0;
		System.out.println("sign="+sign);
		for(int i=0;i<15;i++){
			for(int j=0;j<15;j++){
				if(table[i][j]==sign){
					livethreecount=0;
					//___ooo___
					//down & up
					while (true) {
						if (i > 2 && i < 10) {//___Ooo___
							if ((!(table[i-3][j]==table[i][j]&&table[i+5][j]==table[i][j])) && table[i - 2][j] == ' ' && table[i - 1][j] == ' ' && table[i + 1][j] == table[i][j] && table[i + 2][j] == table[i][j] && table[i + 3][j] == ' ' && table[i + 4][j] == ' ') {
								livethreecount++;
								break;
							}
						}
						if (i > 3 && i < 11) {//___oOo___
							if ((!(table[i-4][j]==table[i][j]&&table[i+4][j]==table[i][j])) && table[i - 3][j] == ' ' && table[i - 2][j] == ' ' && table[i - 1][j] == table[i][j] && table[i + 1][j] == table[i][j] && table[i + 2][j] == ' ' && table[i + 3][j] == ' ') {
								livethreecount++;
								break;
							}
						}
						if (i > 4 && i < 12) {//___ooO___
							if ((!(table[i-5][j]==table[i][j]&&table[i+3][j]==table[i][j])) && table[i - 4][j] == ' ' && table[i - 3][j] == ' ' && table[i - 2][j] == table[i][j] && table[i - 1][j] == table[i][j] && table[i + 1][j] == ' ' && table[i + 2][j] == ' ') {
								livethreecount++;
								break;
							}
						}
						break;
					}
					//left & right
					while (true) {
						if (j > 2 && j < 10) {//___Ooo___
							if ((!(table[i][j-3]==table[i][j]&&table[i][j+5]==table[i][j])) && table[i][j - 2] == ' ' && table[i][j - 1] == ' ' && table[i][j + 1] == table[i][j] && table[i][j + 2] == table[i][j] && table[i][j + 3] == ' ' && table[i][j + 4] == ' ') {
								livethreecount++;
								break;
							}
						}
						if (j > 3 && j < 11) {//___oOo___
							if ((!(table[i][j-4]==table[i][j]&&table[i][j+4]==table[i][j])) && table[i][j - 3] == ' ' && table[i][j - 2] == ' ' && table[i][j - 1] == table[i][j] && table[i][j + 1] == table[i][j] && table[i][j + 2] == ' ' && table[i][j + 3] == ' ') {
								livethreecount++;
								break;
							}
						}
						if (j > 4 && j < 12) {//___ooO___
							if ((!(table[i][j-5]==table[i][j]&&table[i][j+3]==table[i][j])) && table[i][j - 4] == ' ' && table[i][j - 3] == ' ' && table[i][j - 2] == table[i][j] && table[i][j - 1] == table[i][j] && table[i][j + 1] == ' ' && table[i][j + 2] == ' ') {
								livethreecount++;
								break;
							}
						}
						break;
					}
					//left up & right down
					while (true) {
						if (i > 2 && i < 10 && j > 2 && j < 10) {//___Ooo___
							if ((!(table[i-3][j-3]==table[i][j]&&table[i+5][j+5]==table[i][j])) && table[i - 2][j-2] == ' ' && table[i - 1][j-1] == ' ' && table[i + 1][j+1] == table[i][j] && table[i + 2][j+2] == table[i][j] && table[i + 3][j+3] == ' ' && table[i + 4][j+4] == ' ') {
								livethreecount++;
								break;
							}
						}
						if (i > 3 && i < 11 && j > 3 && j < 11) {//___oOo___
							if ((!(table[i-4][j-4]==table[i][j]&&table[i+4][j+4]==table[i][j])) && table[i - 3][j-3] == ' ' && table[i - 2][j-2] == ' ' && table[i - 1][j-1] == table[i][j] && table[i + 1][j+1] == table[i][j] && table[i + 2][j+2] == ' ' && table[i + 3][j+3] == ' ') {
								livethreecount++;
								break;
							}
						}
						if (i > 4 && i < 12 && j > 4 && j < 12) {//___ooO___
							if ((!(table[i-5][j-5]==table[i][j]&&table[i+3][j+3]==table[i][j])) && table[i - 4][j-4] == ' ' && table[i - 3][j-3] == ' ' && table[i - 2][j-2] == table[i][j] && table[i - 1][j-1] == table[i][j] && table[i + 1][j+1] == ' ' && table[i + 2][j+2] == ' ') {
								livethreecount++;
								break;
							}
						}
						break;
					}
					//left down & right up
					while (true) {
						if (i > 2 && i < 10 && j > 4 && j < 12) {//___Ooo___
							if ((!(table[i-3][j+3]==table[i][j]&&table[i+5][j-5]==table[i][j])) && table[i - 2][j+2] == ' ' && table[i - 1][j+1] == ' ' && table[i + 1][j-1] == table[i][j] && table[i + 2][j-2] == table[i][j] && table[i + 3][j-3] == ' ' && table[i + 4][j-4] == ' ') {
								livethreecount++;
								break;
							}
						}
						if (i > 3 && i < 11 && j > 3 && j < 11) {//___oOo___
							if ((!(table[i-4][j+4]==table[i][j]&&table[i+4][j-4]==table[i][j])) && table[i - 3][j+3] == ' ' && table[i - 2][j+2] == ' ' && table[i - 1][j+1] == table[i][j] && table[i + 1][j-1] == table[i][j] && table[i + 2][j-2] == ' ' && table[i + 3][j-3] == ' ') {
								livethreecount++;
								break;
							}
						}
						if (i > 4 && i < 12 && j > 2 && j < 10) {//___ooO___
							if ((!(table[i-5][j+5]==table[i][j]&&table[i+3][j-3]==table[i][j])) && table[i - 4][j+4] == ' ' && table[i - 3][j+3] == ' ' && table[i - 2][j+2] == table[i][j] && table[i - 1][j+1] == table[i][j] && table[i + 1][j-1] == ' ' && table[i + 2][j-2] == ' ') {
								livethreecount++;
								break;
							}
						}
						break;
					}

					//__oo_o__,__o_oo__
					//down & up
					while (true) {
						if (i > 1 && i < 10) {//__Oo_o__,__O_oo__
							if (table[i - 2][j] != table[i][j] && table[i - 1][j] == ' ' && ((table[i + 1][j] == table[i][j] && table[i + 2][j] == ' ') || (table[i + 2][j] == table[i][j] && table[i + 1][j] == ' ')) && table[i + 3][j] == table[i][j] && table[i + 4][j] == ' ' && table[i + 5][j] != table[i][j]) {
								livethreecount++;
								break;
							}
						}
						if (i > 4 && i < 13) {//__oo_O__,__o_oO__
							if (table[i - 5][j] != table[i][j] && table[i - 4][j] == ' ' && table[i - 3][j] == table[i][j] && ((table[i - 2][j] == table[i][j] && table[i - 1][j] == ' ') || (table[i - 1][j] == table[i][j] && table[i - 2][j] == ' ')) && table[i + 1][j] == ' ' && table[i + 2][j] != table[i][j]) {
								livethreecount++;
								break;
							}
						}
						if (i > 2 && i < 11) {//__oO_o__
							if (table[i - 3][j] != table[i][j] && table[i - 2][j] == ' ' && table[i - 1][j] == table[i][j] && table[i + 1][j] == ' ' && table[i + 2][j] == table[i][j] && table[i + 3][j] == ' ' && table[i + 4][j] != table[i][j]) {
								livethreecount++;
								break;
							}
						}
						if (i > 3 && i < 12) {//__o_Oo__
							if (table[i - 4][j] != table[i][j] && table[i - 3][j] == ' ' && table[i - 2][j] == table[i][j] && table[i - 1][j] == ' ' && table[i + 1][j] == table[i][j] && table[i + 2][j] == ' ' && table[i + 3][j] != table[i][j]) {
								livethreecount++;
								break;
							}
						}
						break;
					}
					//left & right
					while (true) {
						if (j > 1 && j < 10) {//__Oo_o__,__O_oo__
							if (table[i][j - 2] != table[i][j] && table[i][j - 1] == ' ' && ((table[i][j + 1] == table[i][j] && table[i][j + 2] == ' ') || (table[i][j + 2] == table[i][j] && table[i][j + 1] == ' ')) && table[i][j + 3] == table[i][j] && table[i][j + 4] == ' ' && table[i][j + 5] != table[i][j]) {
								livethreecount++;
								break;
							}
						}
						if (j > 4 && j < 13) {//__oo_O__,__o_oO__
							if (table[i][j - 5] != table[i][j] && table[i][j - 4] == ' ' && table[i][j - 3] == table[i][j] && ((table[i][j - 2] == table[i][j] && table[i][j - 1] == ' ') || (table[i][j - 1] == table[i][j] && table[i][j - 2] == ' ')) && table[i][j + 1] == ' ' && table[i][j + 2] != table[i][j]) {
								livethreecount++;
								break;
							}
						}
						if (j > 2 && j < 11) {//__oO_o__
							if (table[i][j - 3] != table[i][j] && table[i][j - 2] == ' ' && table[i][j - 1] == table[i][j] && table[i][j + 1] == ' ' && table[i][j + 2] == table[i][j] && table[i][j + 3] == ' ' && table[i][j + 4] != table[i][j]) {
								livethreecount++;
								break;
							}
						}
						if (j > 3 && j < 12) {//__o_Oo__
							if (table[i][j - 4] != table[i][j] && table[i][j - 3] == ' ' && table[i][j - 2] == table[i][j] && table[i][j - 1] == ' ' && table[i][j + 1] == table[i][j] && table[i][j + 2] == ' ' && table[i][j + 3] != table[i][j]) {
								livethreecount++;
								break;
							}
						}
						break;
					}
					//left up & right down
					while (true) {
						if (i > 1 && i < 10 && j > 1 && j < 10) {//__Oo_o__,__O_oo__
							if (table[i - 2][j - 2] != table[i][j] && table[i - 1][j - 1] == ' ' && ((table[i + 1][j + 1] == table[i][j] && table[i + 2][j + 2] == ' ') || (table[i + 2][j + 2] == table[i][j] && table[i + 1][j + 1] == ' ')) && table[i + 3][j + 3] == table[i][j] && table[i + 4][j + 4] == ' ' && table[i + 5][j + 5] != table[i][j]) {
								livethreecount++;
								break;
							}
						}
						if (i > 4 && i < 13 && j > 4 && j < 13) {//__oo_O__,__o_oO__
							if (table[i - 5][j - 5] != table[i][j] && table[i - 4][j - 4] == ' ' && table[i - 3][j - 3] == table[i][j] && ((table[i - 2][j - 2] == table[i][j] && table[i - 1][j - 1] == ' ') || (table[i - 1][j - 1] == table[i][j] && table[i - 2][j - 2] == ' ')) && table[i + 1][j + 1] == ' ' && table[i + 2][j + 2] != table[i][j]) {
								livethreecount++;
								break;
							}
						}
						if (i > 2 && i < 11 && j > 2 && j < 11) {//__oO_o__
							if (table[i-3][j - 3] != table[i][j] && table[i-2][j - 2] == ' ' && table[i-1][j - 1] == table[i][j] && table[i+1][j + 1] == ' ' && table[i+2][j + 2] == table[i][j] && table[i+3][j + 3] == ' ' && table[i+4][j + 4] != table[i][j]) {
								livethreecount++;
								break;
							}
						}
						if (i > 3 && i < 12 && j > 3 && j < 12) {//__o_Oo__
							if (table[i-4][j - 4] != table[i][j] && table[i-3][j - 3] == ' ' && table[i-2][j - 2] == table[i][j] && table[i-1][j - 1] == ' ' && table[i+1][j + 1] == table[i][j] && table[i+2][j + 2] == ' ' && table[i+3][j + 3] != table[i][j]) {
								livethreecount++;
								break;
							}
						}
						break;
					}
					//left down & right up
					while (true) {
						if (i > 1 && i < 10 && j > 4 && j < 13) {//__Oo_o__,__O_oo__
							if (table[i - 2][j + 2] != table[i][j] && table[i - 1][j + 1] == ' ' && ((table[i + 1][j - 1] == table[i][j] && table[i + 2][j - 2] == ' ') || (table[i + 2][j - 2] == table[i][j] && table[i + 1][j - 1] == ' ')) && table[i + 3][j - 3] == table[i][j] && table[i + 4][j - 4] == ' ' && table[i + 5][j - 5] != table[i][j]) {
								livethreecount++;
								break;
							}
						}
						if (i > 4 && i < 13 && j > 1 && j < 10) {//__oo_O__,__o_oO__
							if (table[i - 5][j + 5] != table[i][j] && table[i - 4][j + 4] == ' ' && table[i - 3][j + 3] == table[i][j] && ((table[i - 2][j + 2] == table[i][j] && table[i - 1][j + 1] == ' ') || (table[i - 1][j + 1] == table[i][j] && table[i - 2][j + 2] == ' ')) && table[i + 1][j - 1] == ' ' && table[i + 2][j - 2] != table[i][j]) {
								livethreecount++;
								break;
							}
						}
						if (i > 2 && i < 11 && j > 3 && j < 12) {//__oO_o__
							if (table[i-3][j + 3] != table[i][j] && table[i-2][j + 2] == ' ' && table[i-1][j + 1] == table[i][j] && table[i+1][j - 1] == ' ' && table[i+2][j - 2] == table[i][j] && table[i+3][j - 3] == ' ' && table[i+4][j - 4] != table[i][j]) {
								livethreecount++;
								break;
							}
						}
						if (i > 3 && i < 12 && j > 2 && j < 11) {//__o_Oo__
							if (table[i-4][j + 4] != table[i][j] && table[i-3][j + 3] == ' ' && table[i-2][j + 2] == table[i][j] && table[i-1][j + 1] == ' ' && table[i+1][j - 1] == table[i][j] && table[i+2][j - 2] == ' ' && table[i+3][j - 3] != table[i][j]) {
								livethreecount++;
								break;
							}
						}
						break;
					}
					System.out.println(sign+" ij: "+i+","+j+","+livethreecount);
					if(livethreecount==1){
						saveNum=1;
					}
					if(livethreecount==2){
						return 2;
					}
				}
			}
		}
		return saveNum;
	}
}
class MyServerChatThread extends Thread {
	public Socket socket1;
	public Socket socket2;
	public InputStream in1;
	public InputStream in2;
	public OutputStream out1;
	public OutputStream out2;
	public static ArrayList<Integer> close_arr = new ArrayList<Integer>();
	public int pairId=0;
	public MyServerChatThread(Socket s1,Socket s2,int pair_id) {
		super();
		socket1 = s1;
		socket2 = s2;
		pairId=pair_id;
		try {
			in1 = socket1.getInputStream();
			in2 = socket2.getInputStream();
			out1 = socket1.getOutputStream();
			out2 = socket2.getOutputStream();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			byte[] buffer = new byte[255];
			byte[] onebuffer = new byte[1];
			String sbuffer;
			String endS="leave__________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________";

			while (true) {
				in1.read(buffer);//字串
				sbuffer = new String(buffer);

				out2.write(buffer);
				out2.flush();

				if(sbuffer.equals(endS)){
					System.out.println("chat leave");
					out1.write(buffer);
					out1.flush();

					int closedNum=0;
					for(int i=0;i<close_arr.size();i++){
						if(close_arr.get(i)==pairId){//can close
							//close
							System.out.println("can close");

							out1.close();
							out2.close();
							in1.close();
							in2.close();
							socket1.close();
							socket2.close();

							break;
						}
					}
					close_arr.add(pairId);
					break;
				}
				else {
					System.out.println(sbuffer);
				}

				buffer=new byte[255];
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("chat thread fin.");
	}
}

class MyServerEndGameThread extends Thread {
	public InputStream in;
	public int state;
	public boolean isGet=false;
	public boolean isGeted=false;
	public boolean canGet(){
		return isGet;
	}
	public int get_state(){
		isGeted=true;
		return state;
	}
	public MyServerEndGameThread(InputStream in_) {
		super();
		in = in_;
		state=0;
	}


	public void run() {
		try {
			byte[] buffer = new byte[5];
			String sbuffer;
			System.out.println("wait end game");
			in.read(buffer);//字串

			sbuffer = new String(buffer);
			if(sbuffer.equals("leave")){
				state=-1;
				System.out.println("state=-1");
			}
			else if(sbuffer.equals("reset")) {
				state=1;
				System.out.println("state=1");
			}
			isGet=true;
			System.out.println("end game thread get: "+sbuffer);
			while (!isGeted){
				;
			}
		} catch(Exception e) {
			state=-2;
			isGet=true;
			System.out.println("end game thread error");
			while (!isGeted){
				;
			}
		}
		System.out.println("end game thread fin.");
		System.out.println(state);
	}



}