package test;

import java.util.LinkedList;
import java.util.Scanner;

import javax.naming.InitialContext;

/*
 * 基于"算符优先"文法 的表达式求值， 其实这也是数据程序编译器设计中常用的一种方法
 * 其实可以一次性 扫描中缀表达式的同时，利用算符文法来求出中缀表达式的值，由于中缀式和后缀式的相互转化使用的也是算法文法，这里就把问题分开两部分来做
 * 提前说一下，个人总是感觉，算法设计类的问题总是比较适合面向过程来解决，这里硬是面向对象感觉有点伪面向对象的感觉
 * ExpressionEvaluate    算法的主体驱动部分
 */

public class ExpressionEvaluate {
    
    public static final String OP = "+-*/()#";    //求值系统的所有算附表
    public static final int[][] opPreTable={    //算符优先级表
        {1, 1, -1, -1, -1, 1, 1},
        {1, 1, -1, -1, -1, 1, 1},
        {1, 1, 1, 1, -1, 1, 1},
        {1, 1, 1, 1, -1, 1, 1},
        {-1, -1, -1, -1, -1, 0, Integer.MAX_VALUE},
        {1, 1, 1, 1, Integer.MAX_VALUE, 1, 1},
        {-1, -1, -1, -1, -1, -Integer.MAX_VALUE, 0}
    };
    
    // 定义两个工作栈
    private LinkedList<Character> optr;    // 存储操作符
    private LinkedList<? super Number> opnd;    //存储数字
    private StringBuffer inffixExp;        //    存储中缀表达式
    private StringBuffer suffixExp;        //存储后缀表达式
    private double result;                //表达式最终的结果
    
    {// 构造代码块 优先于 构造函数的执行
        init();    //初始化操作
    }
    public ExpressionEvaluate() {
        
    }
    public ExpressionEvaluate(String exp)
    {
        setInffixExp(exp);
    }
    
    public void setInffixExp (String exp)
    {
        inffixExp = new StringBuffer(exp);
        if(inffixExp.charAt(inffixExp.length()-1)!='#')
            inffixExp.append('#');
    }
    
    private void init()
    {
        inffixExp = new StringBuffer();
        suffixExp = new StringBuffer();
        optr = new LinkedList<Character>();
        opnd = new LinkedList<Number>();
        optr.push('#');
        result = 0;
    }
    
    public String getInffix()
    {
        return new String(inffixExp.substring(0, inffixExp.length()-1));
    }
    public String getSuffix()
    {
        return new String(suffixExp);
    }
    public Double getResult()
    {
        return result;
    }
    
    /*
     * 中缀表达式的 求值计算
     */
    public void inffixCal()
    {
        int index = 0;
        
        while(inffixExp.charAt(index)!='#' || optr.peekFirst()!='#')
        {
            Character ch = inffixExp.charAt(index);
            if(!inOP(ch))
            {// 一般数字
                opnd.push(Double.parseDouble(ch+""));    //于是准备取下一个字符了
                index ++;
            }
            else{// 运算符
                switch(Precede(optr.peekFirst(), ch))
                {
                    case -1:    // < 栈顶 符号 的优先级 低 符号入栈
                        optr.push(ch);
                        index ++;
                        break;
                        
                    case 1:        // > 即栈顶符号的优先级比较高
                        Character theta = optr.pop();
                        Number b = (Double) opnd.pop();
                        Number a = (Double) opnd.pop();
                        Double c = operate(a, b, theta);
                        opnd.push(c);        // 把计算的结果再次压站
                        break;
                        
                    case 0:    // 两种运算符的优先级相等 也就是 ()
                        optr.pop();    //脱括号之后 接着往后扫描
                        index ++;
                        break;
                    default:
                        throw new RuntimeException(new Exception("您的文法有误，请检查"));
                }
            }
        }
        result = (Double)opnd.pop();
    }
    
    public double operate(Number a, Number b, Character theta) {
        double c = 0;
        switch(theta)
        {
            case '+':
                c = (Double)a + (Double)b;
                break;
            case '-':
                c = (Double)a - (Double)b;
                break;
            case '*':
                c = (Double)a * (Double)b;
                break;
            case '/':
                if((Double)b == 0)
                    throw new RuntimeException(new Exception("除数不能为0"));
                c = (Double)a / (Double)b;
                break;
            default:
                throw new RuntimeException(new Exception("尚不支持这种运算"));
        }
        return c;
    }
    /*
     * 比较栈optr栈顶符号 和 当前符号之间的优先级
     */
    public int Precede(Character peekFirst, Character ch) {
        return opPreTable[OP.indexOf(peekFirst)][OP.indexOf(ch)];
    }
    // 判断某个字符是不是在 运算符表中
    public boolean inOP(Character ch)
    {
        return OP.contains(new String (ch+""));
    }
    
    /*
     * 中缀表达式到后缀表达式
     * 和 中缀式 求值 非常相似
     */
    public void inffix2suffix()
    {
        suffixExp.setLength(0);
        optr.clear();
        opnd.clear();
        int index = 0;
        
    }
    
    public static void main(String[] args) {
/*        String exp;
        Scanner scanner = new Scanner(System.in);    // 2*(5-1)/2+3-1
        
        System.out.println("输入一个以#结尾的表达式");
        exp = scanner.next();*/
        
        ExpressionEvaluate ee = new ExpressionEvaluate();
        ee.setInffixExp("2*3*4-1#");
        
        System.out.println(ee.getInffix());
        
        ee.inffixCal();
        
        System.out.println(ee.getResult());
        
        
    }
    
}