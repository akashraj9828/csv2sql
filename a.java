import java.io.*;
import java.lang.reflect.Array;
import java.util.StringTokenizer;

// import java.util.a;
import java.util.*;

class a {
    static Vector<String> header = new Vector<String>();
    static Boolean print_csv = false;
    static String db_name = "airports";
    static String csv_name = "temp.csv";

    public static void main(String args[]) {
        // db_name="airports";
        setHeaders();
        writeSQ();
        // BufferedWriter fout = new BufferedWriter(new FileWriter("out.sql"));
        // fout.write("cbuf");
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

    public static void writeSQ() {
        int count = 0;
        String s;

        Vector<String> data = new Vector<String>();
        try (BufferedReader fin = new BufferedReader(new FileReader(csv_name))) {

            s = fin.readLine();
            s = fin.readLine();// to skip first line cuz its header

            Iterator hi = header.iterator();
            Boolean found = false;// if " found its set to true until another " is found in array
            Boolean can_combine = false;
            int start = 0;
            int end = 0;
            int combine_count = 0; //delete these number of elemnt at the end
            int i;
            Boolean action = true;
            // Boolean action =false;
            count++;

            String p[] = s.split(",");
            // Vector<String> strVector = new Vector<String>(Arrays.asList(strArray));
            print("size: " + p.length);

            for (i = 0; i < p.length; i++) {
                can_combine = false;
                if (p[i].length() > 0) {

                    if (p[i].charAt(0) == '"') {
                        if (!found) {
                            found = true;
                            start = i;
                            print("start: " + start);
                        }
                    }
                    if (p[i].charAt(p[i].length() - 1) == '"') {
                        if (found) {
                            found = false;
                            end = i;
                            // combine++;
                            can_combine = true;
                            print("end: " + end);
                        }
                    }

                    if (can_combine) {
                        for (int j = start; j < end; j++) {
                            print(j);
                            p[start] += "," + p[j + 1];
                            // print(p[start]);
                        }
                        for (int j = start + 1, k=end+1; k<p.length; j++,k++) {
                            // System.arraycopy(p, j+1, dest, destPos, length);
                            // int dif = end - start;
                            p[j] = p[k];
                            
                        }
                        
                    }

                }
            }

            // print(p[11]);
            // String p[]=s.split("[\",]|\".+\"");
            // String p[]=s.split("\".+\"");
            print("header: " + header.size() + "\t cols: " + p.length);
            for (String x : p) {
                String temp = x;
                // if (temp.matches("-?\\d+"))
                // print(hi.next() + ": " + temp + ",");
                // else
                // print(hi.next() + ": '" + temp + "',");
                System.out.println(x);

            }

            s = fin.readLine();

        } catch (Exception e) {
        }

    }

    public static void writeSQL() {
        int count = 0;
        String s;

        Vector<String> data = new Vector<String>();
        try (BufferedReader fin = new BufferedReader(new FileReader(csv_name))) {
            // BufferedReader fin=new BuffereReader( new FileReader("airport.csv"));
            try (BufferedWriter fout = new BufferedWriter(new FileWriter("out.sql"))) {

                s = fin.readLine();
                s = fin.readLine();// to skip first line cuz its header

                for (String h : header) {
                    print(h);
                }
                // fout.write();

                while (s != null) {
                    StringBuilder sql = new StringBuilder("\n\n");
                    StringTokenizer st = new StringTokenizer(s, ",");

                    count++;
                    sql.append("/* INSERT QUERY NO: " + count + " */\n");
                    sql.append("INSERT INTO " + db_name + "(");

                    for (String temp : header) {
                        sql.append(temp + ",");
                    }

                    sql.append(")\n VALUES \n (");

                    while (st.hasMoreTokens()) {
                        String temp = st.nextToken();
                        if (temp.matches("-?\\d+"))
                            sql.append("" + temp + ",");
                        else
                            sql.append("'" + temp + "',");
                    }

                    sql.append("\n);");

                    print(sql.toString());
                    // fout.write(sql.toString());
                    // if (print_csv)
                    print(s);
                    s = fin.readLine();
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
}