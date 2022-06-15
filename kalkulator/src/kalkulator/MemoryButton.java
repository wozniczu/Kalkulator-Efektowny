package kalkulator;

import java.awt.Button;

public class MemoryButton extends Button
{
Kalkulator cl;

MemoryButton(int x,int y, int width,int height,String cap, Kalkulator calc)
{
super(cap);
setBounds(x,y,width,height);
cl=calc;
cl.add(this);
}
}