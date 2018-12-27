//  String p[] = s.split(",");
//             // Vector<String> strVector = new Vector<String>(Arrays.asList(strArray));
//             print("size: " + p.length);

//             for (i = 0; i < p.length; i++) {
//                 can_combine = false;
//                 if (p[i].length() > 0) {

//                     if (p[i].charAt(0) == '"') {
//                         if (!found) {
//                             found = true;
//                             start = i;
//                             print("start: " + start);
//                         }
//                     }
//                     if (p[i].charAt(p[i].length() - 1) == '"') {
//                         if (found) {
//                             found = false;
//                             end = i;
//                             // combine++;
//                             can_combine = true;
//                             print("end: " + end);
//                         }
//                     }

//                     if (can_combine) {
//                         count+=(end-start);
//                         for (int j = start; j < end; j++) {
//                             print(j);
//                             p[start] += "," + p[j + 1];
//                             // print(p[start]);
//                         }
//                         for (int j = start + 1, k=end+1; k<p.length; j++,k++) {
//                             // System.arraycopy(p, j+1, dest, destPos, length);
//                             // int dif = end - start;
//                             p[j] = p[k];

//                         }

//                     }

//                 }
//             }

class x {
    public static void writeSQ() {
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
                        sql.append(temp + ",");
                    }
                    sql.append(")\n VALUES \n (");

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

                    // print("header: " + header.size() + "\t cols: " + p.size());
                    for (String x : p) {
                        String temp = x;
                        if (temp.matches("-?\\d+"))
                            sql.append("" + temp + ",");
                        else
                            sql.append("'" + temp + "',");
                        // System.out.println(temp);

                    }

                    sql.append("\n);");
                    print(sql.toString());

                    // print("count:" + combine_count);
                    s = fin.readLine();
                }
            }
        } catch (Exception e) {
        }

    }

}