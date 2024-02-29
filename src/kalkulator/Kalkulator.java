package kalkulator;
import java.awt.*;

import java.awt.event.*;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

public class Kalkulator extends Frame
{
	int test=0;
	private int memCount = 0;
	private double mem[] = new double[100];
	private String text = "";
	private double result = 0;
	String aButtons[] = {"7", "8", "9", "4", "5", "6", "1", "2", "3", "0"};
	String cButtons[] = {"/", "sqrt", "*", "%", "-", "+", "=","CE","." };
	String bButtons[] = {"MR", "MD", "MS","H" };	
	DigitButton digitButton[] = new DigitButton[aButtons.length];
	OperatorButton operatorButton[] = new OperatorButton[cButtons.length];
	MemoryButton memoryButton[] = new MemoryButton[bButtons.length];
	Label digitLabel = new Label("0",Label.RIGHT);	
	final int HEIGHT = 30, WIDTH=30, X_GAP=10,Y_GAP=5,TOP_X=30, TOP_Y=50;;


	public static double eval(final String str) {
	    return new Object() {
	        int pos = -1, ch;
	        
	        void nextChar() {
	            ch = (++pos < str.length()) ? str.charAt(pos) : -1;
	        }
	        
	        boolean eat(int charToEat) {
	            while (ch == ' ') nextChar();
	            if (ch == charToEat) {
	                nextChar();
	                return true;
	            }
	            return false;
	        }
	        
	        double parse() {
	            nextChar();
	            double x = parseExpression();
	            if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
	            return x;
	        }
	          
	        double parseExpression() {
	            double x = parseTerm();
	            for (;;) {
	                if      (eat('+')) x += parseTerm(); // addition
	                else if (eat('-')) x -= parseTerm(); // subtraction
	                else return x;
	            }
	        }
	        
	        double parseTerm() {
	            double x = parseFactor();
	            for (;;) {
	                if      (eat('*')) x *= parseFactor(); // multiplication
	                else if (eat('/')) x /= parseFactor(); // division
	                else if (eat('%')) x %= parseFactor(); 
	                else return x;
	            }
	        }
	        
	        double parseFactor() {
	            if (eat('+')) return +parseFactor(); // unary plus
	            if (eat('-')) return -parseFactor(); // unary minus
	            
	            double x;
	            int startPos = this.pos;
	            if (eat('(')) { // parentheses
	                x = parseExpression();
	                if (!eat(')')) throw new RuntimeException("Missing ')'");
	            } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
	                while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
	                x = Double.parseDouble(str.substring(startPos, this.pos));
	            } else if (ch >= 'a' && ch <= 'z') { // functions
	                while (ch >= 'a' && ch <= 'z') nextChar();
	                String func = str.substring(startPos, this.pos);
	                if (eat('(')) {
	                    x = parseExpression();
	                    if (!eat(')')) throw new RuntimeException("Missing ')' after argument to " + func);
	                } else {
	                    x = parseFactor();
	                }
	                if (func.equals("sqrt")) x = Math.sqrt(x);
	            } else {
	                throw new RuntimeException("Unexpected: " + (char)ch);
	            }            
	            return x;
	        }
	    }.parse();
	}
	
	Kalkulator(String frameText)
	{
	super(frameText);
	setLayout(null);
	setSize(325,370);
	setVisible(true);
	addKeyListener(new Keychecker());
	
	int x=TOP_X, y=TOP_Y;
	digitLabel.setBounds(x,y+20,260,50);
	digitLabel.setBackground(Color.BLACK);
	digitLabel.setForeground(Color.WHITE);
	add(digitLabel);
	//memory
	x=TOP_X-25;	
	y=TOP_Y+2*(HEIGHT+Y_GAP)+5;
	for(int i=0; i<memoryButton.length-1; i++)
	{
		memoryButton[i]=new MemoryButton(x+250,y+10,WIDTH+10,HEIGHT,bButtons[i], this);
		memoryButton[i].setForeground(Color.RED);
		y+=HEIGHT+Y_GAP;
		memoryButton[i].setFocusable(false);
	}
	memoryButton[3]=new MemoryButton(x+250,y+45,WIDTH+10,HEIGHT,bButtons[3], this);
	memoryButton[3].setFocusable(false);
	
	//operator
	x=TOP_X+1*(WIDTH+X_GAP)+30;
	y=TOP_Y+1*(HEIGHT+Y_GAP);
	operatorButton[0]=new OperatorButton(x+95,y+50,WIDTH+10,HEIGHT,cButtons[0], this);
	operatorButton[1]=new OperatorButton(x+15,y+50,WIDTH+10,HEIGHT,cButtons[1], this);
	operatorButton[2]=new OperatorButton(x+95,y+85,WIDTH+10,HEIGHT,cButtons[2], this);
	operatorButton[3]=new OperatorButton(x+55,y+50,WIDTH+10,HEIGHT,cButtons[3], this);
	operatorButton[4]=new OperatorButton(x+95,y+120,WIDTH+10,HEIGHT,cButtons[4], this);
	operatorButton[5]=new OperatorButton(x+95,y+155,WIDTH+10,65,cButtons[5], this);
	operatorButton[6]=new OperatorButton(x+55,y+190,WIDTH+10,HEIGHT,cButtons[6], this);
	operatorButton[7]=new OperatorButton(x-25,y+50,WIDTH+10,HEIGHT,cButtons[7], this);
	operatorButton[8]=new OperatorButton(x+15,y+190,WIDTH+10,HEIGHT,cButtons[8], this);
	for(int i=0;i<operatorButton.length;i++)
	{
		operatorButton[i].setFocusable(false);
	}
	
	//digit
	x=TOP_X+WIDTH+X_GAP+30;
	y=TOP_Y+2*(HEIGHT+Y_GAP);
	for(int i=0;i<digitButton.length;i++)
	{
		digitButton[i]=new DigitButton(x-25,y+50,WIDTH+10,HEIGHT,aButtons[i], this);
		digitButton[i].setForeground(Color.CYAN);
		x+=WIDTH+X_GAP;
		if((i+1)%3==0){x=TOP_X+WIDTH+X_GAP+30;; y+=HEIGHT+Y_GAP;}
		digitButton[i].setFocusable(false);
	}
	
	
	addWindowListener(new WindowAdapter()
	{
	public void windowClosing(WindowEvent ev)
	{System.exit(0);}
	});
	
	operatorButton[0].addActionListener( new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
	    	text+="/";
	    	digitLabel.setText(text);
	    	
	    }
	});
	
	operatorButton[1].addActionListener( new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
	    	text+="sqrt";
	    	digitLabel.setText(text);
	    	
	    }
	});
	
	operatorButton[2].addActionListener( new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
	    	text+="*";
	    	digitLabel.setText(text);
	    	
	    }
	});
	
	operatorButton[3].addActionListener( new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
	    	text+="%";
	    	digitLabel.setText(text);
	    	
	    }
	});
	
	operatorButton[4].addActionListener( new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
	    	text+="-";
	    	digitLabel.setText(text);
	    	
	    }
	});
	
	operatorButton[5].addActionListener( new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
	    	text+="+";
	    	digitLabel.setText(text);
	    	
	    }
	});
	
	operatorButton[6].addActionListener( new ActionListener()
	{
	    public void actionPerformed(ActionEvent e) 
	    {
	    	if(text.endsWith("0")||text.endsWith("1")||text.endsWith("2")||text.endsWith("3")||text.endsWith("4")||text.endsWith("5")||text.endsWith("6")||text.endsWith("7")||text.endsWith("8")||text.endsWith("9"))
        	{
	    		result = eval(text);
		    	if((result-(int)result==0)&&prime((int)result))
		    	{
		    		for(int j=(int)(result);j!=0;j--)
		    			Print();
		    	}
		    	text=String.valueOf(result);
		    	digitLabel.setText(text);
        	}
	    	
	    }
	});
	
	operatorButton[7].addActionListener( new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
	    	text="";
	    	result = 0;
	    	digitLabel.setText(text);
	    	digitLabel.setText("0");
	    	
	    }
	});
	
	operatorButton[8].addActionListener( new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
	    	if(!(text.endsWith(".")))
	    	{
		    	text+=".";
		    	digitLabel.setText(text);
	    	}
	    	if(text=="")
	    	{
		    	text+="0.";
		    	digitLabel.setText(text);
	    	}
	    }
	});
	
	digitButton[0].addActionListener( new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
	    	digitLabel.setText("");
	    	text+="7";
	    	digitLabel.setText(text);
	    	
	    }
	});
	digitButton[1].addActionListener( new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
	    	digitLabel.setText("");
	    	text+="8";
	    	digitLabel.setText(text);
	    	
	    }
	});
	digitButton[2].addActionListener( new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
	    	digitLabel.setText("");
	    	text+="9";
	    	digitLabel.setText(text);
	    	
	    }
	});
	digitButton[3].addActionListener( new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
	    	digitLabel.setText("");
	    	text+="4";
	    	digitLabel.setText(text);
	    	
	    }
	});
	digitButton[4].addActionListener( new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
	    	digitLabel.setText("");
	    	text+="5";
	    	digitLabel.setText(text);
	    	
	    }
	});
	digitButton[5].addActionListener( new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
	    	digitLabel.setText("");
	    	text+="6";
	    	digitLabel.setText(text);
	    	
	    }
	});
	digitButton[6].addActionListener( new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
	    	digitLabel.setText("");
	    	text+="1";
	    	digitLabel.setText(text);
	    	
	    }
	});
	digitButton[7].addActionListener( new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
	    	digitLabel.setText("");
	    	text+="2";
	    	digitLabel.setText(text);
	    	
	    }
	});
	digitButton[8].addActionListener( new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
	    	digitLabel.setText("");
	    	text+="3";
	    	digitLabel.setText(text);
	    	
	    }
	});
	digitButton[9].addActionListener( new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
	    	digitLabel.setText("");
	    	text+="0";
	    	digitLabel.setText(text);
	    	
	    }
	});
	
	memoryButton[3].addActionListener( new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
	    	JFrame frame = new JFrame("Instrukcja");
	    	frame.setLayout(new GridLayout(5,1));
	    	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	        frame.setVisible(true);
	        frame.setResizable(false);
	        frame.setSize(500,300);
	        JLabel label1 = new JLabel("MR --- Wyświetla na ekran ostatnia zapisana liczbe."); 
	        JLabel label2 = new JLabel("MD --- Usuwa z pamieci ostatnia zapisana liczbe.");
	        JLabel label3 = new JLabel("MS --- Zapisuje w pamieci aktualnie wyswietlana liczbe.");
	        frame.add(label1);
	        frame.add(label2);
	        frame.add(label3);
	    }
	});
	
	memoryButton[2].addActionListener( new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
	    	if(text!="")
	    	{
		    	mem[memCount]=eval(text);
		    	memCount++;	 
	    	}
	    	else
	    	{
	    		mem[memCount]=0;
		    	memCount++;	
	    	}
	    }
	});
	
	memoryButton[1].addActionListener( new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
	    	if(memCount!=0)
	    	{
		    	mem[memCount]=0;
		    	memCount--;	    	
	    	}
	    		
	    }
	});
	
	memoryButton[0].addActionListener( new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
	    	if(memCount!=0)
	    	{
		    	text=String.valueOf(mem[memCount-1]);
		    	digitLabel.setText(text);	
	    	}

	    }
	});
	
}
	
	public void Print(){
		Insets abc=getInsets();

	 	Graphics rys=getGraphics();
	 	Random gen=new Random();
	 	int sx,sy,x,y;
	 	sx=gen.nextInt(abc.left+getWidth()-abc.right-20);
	 	sy=gen.nextInt(abc.top+getHeight()-100-abc.bottom);
	 	x=gen.nextInt(getWidth()-sx-10);
	 	y=gen.nextInt(getHeight()-sy);
	 	
		rys.setColor(new Color(gen.nextInt(255),gen.nextInt(255),gen.nextInt(255)));
	 	int i=gen.nextInt(2);
	 	
 		if(i==1){rys.fillOval(sx,sy,x,y);}
 		if(i==2){rys.fillRect(sx,sy,x,y);}
 		else {rys.fillRoundRect(sx,sy,x,y,x/2, y/2);}
	 		
	 }	
	

public static void main(String []args)
{
Kalkulator kalkulator = new Kalkulator("Kalkulator");
kalkulator.setResizable(false);
}

class Keychecker extends KeyAdapter{

	private char ch='\0';
    @Override
    public void keyPressed(KeyEvent event) {
        ch = event.getKeyChar();
        if (ch=='1'||ch=='2'||ch=='3'||ch=='4'||ch=='5'||ch=='6'||ch=='7'||ch=='8'||ch=='9'||ch=='0'||ch=='+'||ch=='-'||ch=='/'||ch=='*'||ch=='%')
	        digitLabel.setText(text+=Character.toString(ch));
        else if ((event.getKeyCode()==10)||(event.getKeyChar()=='='))
        {
        	if(text.endsWith("0")||text.endsWith("1")||text.endsWith("2")||text.endsWith("3")||text.endsWith("4")||text.endsWith("5")||text.endsWith("6")||text.endsWith("7")||text.endsWith("8")||text.endsWith("9"))
        	{
        		result = eval(text);
		    	if((result-(int)result==0)&&prime((int)result))
		    	{
		    		for(int j=(int)(result);j!=0;j--)
		    			Print();
		    	}
		    	text=String.valueOf(result);
		    	digitLabel.setText(text);
        	}     	
        }
        else if ((event.getKeyCode()==8)&&(text!=""))
        {
        	text = text.substring(0, text.length()-1);
	    	digitLabel.setText(text);
        }
        else if ((ch=='.'))
        {
        	if(!(text.endsWith(".")))
	    	{
		    	text+=".";
		    	digitLabel.setText(text);
	    	}
	    	if(text=="")
	    	{
		    	text+="0.";
		    	digitLabel.setText(text);
	    	}
        }
    }
}


public boolean prime(int n)
{
	if(n<2)
		return false; 
		
	for(int i=2;i*i<=n;i++)
		if(n%i==0)
			return false;
	return true;
}
}
