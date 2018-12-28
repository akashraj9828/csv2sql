import java.io.*;
import java.lang.reflect.Array;
import java.util.StringTokenizer;
import java.util.*;

class CSV2SQL {
    static Vector<String> header = new Vector<String>();
    static Boolean print_csv = false;
    static Boolean print_header = true;
    static Boolean verbose = true;
    static Boolean ignoreError = false;
    static String table_name = "TABLE_NAME";
    static String csv_name = "in.csv"; // input file
    static String sql_name = "out.sql"; // output file

    /*
     * args [0]=input file.csv [1]=output file.sql [2]=table name [3]=other args
     * like show header or to make update sql
     * 
     */
    public static void main(String args[]) {

        if (args.length > 0)
            init(args);
        print_info();
        setHeaders();
        writeSQL();
    }

    public static void init(String args[]) {
        csv_name = args[0];
        sql_name = args[1];
        table_name = args[2];
    }

    public static void print_info() {
        print("^^^^^^^^^^^^^^^^^^^^^^^^");
        print("Input file:- " + csv_name);
        print("Output file:- " + sql_name);
        print("Table name:- " + table_name);
        print("Ignore error:- "+ignoreError);
        print("Print header:- "+print_header);
        print("^^^^^^^^^^^^^^^^^^^^^^^^");

    }

    public static void setHeaders() {
        String s;

        try (BufferedReader fin = new BufferedReader(new FileReader(csv_name))) {
            s = fin.readLine();
            StringTokenizer st = new StringTokenizer(s, ",");
            while (st.hasMoreTokens()) {
                header.add(st.nextToken());
            }

            if (print_header)
                print("\n\n~~~~~~~~ HEADERS ~~~~~~~~\n\n" + header.toString()
                        + "\n\n~~~~~~~~         ~~~~~~~~\n");

        } catch (Exception e) {
            print(e);
        }
    }

    public static void writeSQL() {

        int count = 0; // counts querry number
        String s; // stores line

        try (BufferedReader fin = new BufferedReader(new FileReader(csv_name))) {
            try (BufferedWriter fout = new BufferedWriter(new FileWriter(sql_name))) {
                s = fin.readLine(); // header //and skip it cuz its header

                // main loop //iterates over every line
                while (s != null) {
                    count++;
                    s = fin.readLine();// read nextline //skips first line cuz its header

                    StringBuilder sql = new StringBuilder("\n");
                    String subs[] = s.split(",");
                    Vector<String> data = new Vector<String>(Arrays.asList(subs)); // convert array subs to vector for
                                                                                   // easy handling
                    Boolean found = false;// if " found its set to true until another " is found in array
                    Boolean can_combine = false; // when true combines sub strings that got sepereated
                    int start = 0; // index of where string starts
                    int end = 0; // index of where string ends
                    int combine_count = 0; // total number of blocks joined(delete these number of elemnt at the end)
                    int i;
                    Boolean lengthMismatch = false;

                    // print("size: " + p.size());

                    // to join elements that is string and contains ["] :example:["hello,world"]
                    // if we use split with [,] delemiter then ["hello] [world"] are seperated
                    // this loop goes over the elements and join broken string into one.
                    for (i = 0; i < data.size(); i++) {
                        can_combine = false;

                        if (data.get(i).length() > 0) {
                            char firstChar = data.get(i).charAt(0);
                            char lastChar = data.get(i).charAt(data.get(i).length() - 1);
                            if (firstChar == '"' && !found) {
                                found = true;
                                start = i;
                                // print("start:" + start);
                            }
                            if (lastChar == '"' && found) {
                                found = false;
                                can_combine = true;
                                end = i;
                                // print("end: " + end);
                            }

                            if (can_combine) {
                                combine_count += (end - start);
                                // join elements><
                                for (int j = start; j < end; j++) {
                                    data.set(start, data.get(start) + "," + data.get(j + 1));
                                }
                                // shift elements <--
                                for (int j = start + 1, k = end + 1; k < data.size(); j++, k++) {
                                    data.set(j, data.get(k));
                                }
                            }

                        }
                    }
                    // deletes the extra elements remaining after process.
                    for (int m = 0; m < combine_count; m++) {
                        int last = data.size() - 1;
                        data.remove(last);
                    }

                    // print("header size: "+header.size());
                    if (data.size() != header.size()) {
                        // String err = "";
                        // err += ":::ERROR AT CSV LINE : " + (count + 1) + "\n";
                        // err += ":::::Expected no. of elements: " + header.size() + " no.of elements:
                        // " + data.size()
                        // + "\n";
                        // print(err);

                        if (!ignoreError) {
                            lengthMismatch = true;
                            print("Length mistatch! - Ignoring line " + (count + 1) + " :querry no. " + count);
                        } else {
                            print("Length mistatch! - line " + (count + 1) + " :querry no. " + count);
                            print("ignoring error anyway");
                        }
                    }
                    // print("header: " + header.size() + "\t cols: " + p.size());
                    if (!lengthMismatch) {
                        sql.append("/* INSERT QUERY NO: " + count + "*/\n");
                        sql.append("INSERT INTO " + table_name + "(");
                        for (String temp : header) {
                            if (header.lastElement() != temp)
                                sql.append("`" + temp + "`,");
                            else
                                sql.append("`" + temp + "`");
                        }
                        sql.append(")\nVALUES \n(");

                        for (String x : data) {
                            String temp = x;
                            temp = temp.replace("'", "`");
                            // print(temp);
                            if (temp.matches("-?\\d+"))
                                sql.append("" + temp + "");
                            else
                                sql.append("'" + temp + "'");
                            if (data.lastElement() != x)
                                sql.append(",");
                            else
                                sql.append("");
                            // System.out.println(temp);

                        }
                        sql.append("\n);");
                        // print(sql.toString());
                        fout.write(sql.toString());
                        // print("count:" + combine_count);
                    }
                }
            }
        } catch (Exception e) {
            print(e);
            e.printStackTrace();
        }

    }


    public static void print(Object o) {

        System.out.println(o);
    }

}
