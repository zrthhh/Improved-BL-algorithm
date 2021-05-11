package BACKPACK;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

class Item{
    //物品的尺寸
    public int Point_a1;
    public int Point_b1;//左上角的点

    public int Point_a2;
    public int Point_b2;//右下角的点
    boolean flag1;//标记该物品是否装入箱中
    Item(){

    }
}

class Bin{
    //装箱的尺寸
    public int length_x;
    public int length_y;
    public boolean flag2;//标记箱内该店是被装满

    public Bin(int x,int y){
        this.length_x = x;
        this.length_y = y;
    }
    Bin(){

    }
}

public class BinPacking {
    public Stack<Item> stack = new Stack();//记录装入箱中的物品

    //读取文件中物品的信息--通过坐标来表示物品尺寸
    public Item[] Read_items()throws Exception {
        File file = new File("C:\\Users\\Z.R.T\\Desktop\\data_items.txt");
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file));
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        List<Integer> list = new LinkedList();
        String str = null;
        while ((str = bufferedReader.readLine()) != null) {
            String[] s = str.split(",");
            for (String value : s) {
                int temp = Integer.parseInt(value);
                list.add(temp);
            }
        }

        int len = list.size();
        Item[] items = new Item[len/4];
        for (int i = 0;i < len/4;i++){
            items[i] = new Item();
            int index = i*4;
            items[i].Point_a1 = list.get(index);//左上角横坐标
            items[i].Point_b1 = list.get(index+1);//左上角纵坐标
            items[i].Point_a2 = list.get(index+2);//右下角横坐标
            items[i].Point_b2 = list.get(index+3);//右下角纵坐标
        }

        for (int i = 0;i < items.length;i++){
            items[i].flag1 = false;//标记物品均没有放入箱中
        }
        return items;
    }

    //计算物品的面积
    public double Size(Item item1){
        double a = Math.abs(item1.Point_a1 - item1.Point_a2);
        double b = Math.abs(item1.Point_b1 - item1.Point_b2);
        return a*b;
    }

    //将物品装入箱中
    public void BL(Item[] items,Bin[][] bins,Bin bin){

        //对物品尺寸行进排序
        BinPacking bp = new BinPacking();
        int index = 0;
        for (int i = 0;i < items.length;i++){
            if (bp.Size(items[i]) <= bp.Size(items[index])){
                index = i;
            }
        }
        for (int i = 0;i < items.length;i++){
            if (bp.Size(items[i]) >= bp.Size(items[index]) && !items[i].flag1)
                index = i;//表示尺寸最大物品的标记
        }

        //计算物品的高度和宽度
        int hight = Math.abs((items[index].Point_b1 - items[index].Point_b2));//物品高度
        int wide  = Math.abs((items[index].Point_a1 - items[index].Point_a2));//物品宽度

        //将物品平移到箱子的右上角，然后选取物品左下角作为装入起点
        int start_x = bin.length_x - wide-1;
        int start_y = bin.length_y -1;

        //向箱子左下角移动
        //先向底部移动
        while (start_y > 0){
            start_y--;
            if (!bins[start_x ][start_y ].flag2){
                start_y = start_y+1;
                break;
            }
        }
        //再向左侧移动
        while (start_x > 0){
            start_x--;
            if (!bins[start_x ][start_y ].flag2){
                start_x = start_x+1;
                break;
            }
        }

        //物品移动到左下角的坐标
        int end_x = start_x;
        int end_y = start_y;

        if ((end_x + wide) >= bin.length_x || (end_y + hight) >= bin.length_y){
            return;
        }else {
            //标记物品放入区域的点为已选中
            for (int i = end_x;i < (end_x+wide);i++){
                for (int j = end_y; j < (end_y+hight);j++){
                    bins[i][j].flag2 = false;//标记该区域选中了物品
                }
            }
            items[index].flag1 = true;//标记该物品已经装入箱中
            //更新坐标
            items[index].Point_a1 = end_x;
            items[index].Point_b1 = (end_y+hight);

            stack.add(items[index]);//将选中物品加入栈中
            System.out.println("装入物品的编号：" + index);
            System.out.println("装入箱后矩形的坐标：");
            System.out.println("左下角" + "(" + end_x + "," + end_y + ")");
            System.out.println("右上角" + "(" + (end_x + wide) + "," + (end_y + hight) + ")");
            System.out.println("---------");
        }
    }

    //测试类
    public static void main(String[] args) throws Exception{
        //创建空箱
        Bin bin = new Bin(15,12);
        Bin[][] axis = new Bin[bin.length_x][bin.length_y];
        for (int i = 0;i < bin.length_x;i++){
            for (int j = 0;j < bin.length_y;j++){
                axis[i][j] = new Bin();
                axis[i][j].flag2 = true;//箱中没有装任何物品
            }
        }

        //读取物品信息
        BinPacking bp = new BinPacking();
        Item[] items = bp.Read_items();

        //开始装箱
        for (int i = 0;i < items.length;i++){
            bp.BL(items,axis,bin);
        }
    }
}
