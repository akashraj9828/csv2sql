import java.io.*;
import java.lang.reflect.Array;
import java.util.StringTokenizer;

// import java.util.a;
import java.util.*;

class ParametersMismatch extends Exception{
    
}
class a {
    static Vector<String> header = new Vector<String>();
    static Boolean print_csv = false;
    static String db_name = "airports";
    static String csv_name = "temp.csv";

    public static void main(String args[]) {
        setHeaders();
        writeSQL();
    }

    public static void setHeaders() {
        String s;

        try (BufferedReader fin = new BufferedReader(new FileReader(csv_name))) {
            // BufferedReader fin=new BuffereReader( new FileReader("airport.csv"));
            // FileWriter fout=new FileWriter("out.sql");

            s = fin.readLine();
            StringTokenizer st = new StringTokenizer(s, ",");
            while (st.hasMoreTokens()) {
                header.add(st.nextToken());
            }

            // print("HEADERS::");
            // for (String h : header) {
            // print(h);
            // }

        } catch (Exception e) {

        }
    }

    public static void writeSQL() {
        int count = 0;
        String s;

        Vector<String> data = new Vector<String>();
        try (BufferedReader fin = new BufferedReader(new FileReader(csv_name))) {
            try (BufferedWriter fout = new BufferedWriter(new FileWriter("out.sql"))) {

                s = fin.readLine();
                s = fin.readLine();// to skip first line cuz its header

                Iterator hi = header.iterator();
                Boolean found = false;// if " found its set to true until another " is found in array
                Boolean can_combine = false;
                int start = 0;
                int end = 0;
                int combine_count = 0; // delete these number of elemnt at the end
                int i;
                Boolean action = true;
                // Boolean action =false;

                while (s != null) {
                    StringBuilder sql = new StringBuilder("\n\n");
                    String subs[] = s.split(",");
                    Vector<String> p = new Vector<String>(Arrays.asList(subs));
                    // StringTokenizer st = new StringTokenizer(s, ",");

                    count++;
                    sql.append("/* INSERT QUERY NO: " + count + " */\n");
                    sql.append("INSERT INTO " + db_name + "(");
                    for (String temp : header) {
                        if(header.lastElement()!=temp)
                        sql.append(temp + ",");
                        else
                        sql.append(temp + "");
                    }
                    sql.append(")\nVALUES \n(");

                    // print("size: " + p.size());

                    for (i = 0; i < p.size(); i++) {
                        can_combine = false;
                        // char first_element=
                        if (p.get(i).length() > 0) {

                            if (p.get(i).charAt(0) == '"') {
                                if (!found) {
                                    found = true;
                                    start = i;
                                    // print("start: " + start);
                                }
                            }
                            if (p.get(i).charAt(p.get(i).length() - 1) == '"') {
                                if (found) {
                                    found = false;
                                    end = i;
                                    // combine++;
                                    can_combine = true;
                                    // print("end: " + end);
                                }
                            }

                            if (can_combine) {
                                combine_count += (end - start);
                                for (int j = start; j < end; j++) {
                                    // print(j);
                                    p.set(start, p.get(start) + "," + p.get(j + 1));
                                    // p.get(start) + "," + p[j + 1];
                                    // print(p[start]);
                                }
                                for (int j = start + 1, k = end + 1; k < p.size(); j++, k++) {
                                    // System.arraycopy(p, j+1, dest, destPos, length);
                                    // int dif = end - start;
                                    p.set(j, p.get(k));
                                    // p[j] = p[k];

                                }

                            }

                        }
                    }

                    for (int m = 0; m < combine_count; m++) {
                        int last = p.size() - 1;
                        p.remove(last);
                    }
                    print("header size: "+header.size());
                    if (p.size() != header.size()) {
                        print("ERROR AT CSV LINE : " + (count+1) + "no. elements don't match no. elements in header : size :"+p.size());
                        print(p.toString());

                    }
                    // print("header: " + header.size() + "\t cols: " + p.size());
                    
                    for (String x : p) {
                        String temp = x;
                        // print(temp);                        
                        if (temp.matches("-?\\d+"))
                            sql.append("" + temp + "");
                        else
                            sql.append("`" + temp + "`");
                        if(p.lastElement()!=x)
                        sql.append(",");
                        else
                        sql.append("");
                        // System.out.println(temp);

                    }

                    sql.append("\n);");
                    // print(sql.toString());
                    fout.write(sql.toString());
                    // print("count:" + combine_count);
                    s = fin.readLine();
                    combine_count=0;
                }
            }
        } catch (Exception e) {
        }

    }

   
    public static void print(String args[]) {

        for (int i = 0; i < args.length; i++) {
            System.out.print(args[i]);
        }
    }

    public static void print(String a) {

        System.out.println(a);
    }

    public static void print(int a) {

        System.out.println(a);
    }
    public static void print(Object o) {

        System.out.println(o);
    }

}

