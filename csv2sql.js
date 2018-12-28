var viewname, editorAce, editorResult, dataTable = "", old = "", converted = "";
function performShowHideToHtml(e) {
    null == e && (e = "editor"),
        $("#" + e).hasClass("hide") ? ($("#" + e).removeClass("hide"),
            $("#result1").addClass("hide"),
            $("#showHideBtn").val("Show Output")) : ($("#result1").removeClass("hide"),
                $("#" + e).addClass("hide"),
                $("#showHideBtn").val("Show HTML"))
}
function setToEditor(e) {
    "json-to-html-converter" == viewname && (e = vkbeautify.json(e)),
        editorAce.setValue(e),
        "csv-to-json" == viewname ? csv2xml_json("json") : "csv-to-html" == viewname ? toHTML() : "csv-to-xml-json" == viewname ? csv2xml_json("xml") : "csv-to-sql-converter" == viewname ? csvTosql("Insert") : "csv-to-xml" == viewname ? csv2xml_json("xml") : "csv-to-multi-line-converter" == viewname && csvToMultiline()
}
function convertToHtml(e) {
    var t = editorAce.getValue()
        , r = null;
    if (null != t && 0 != t.length)
        if ("json" == e.toLowerCase())
            r = jsonTocsvbyjson(t, !0),
                toHTML(r, e);
        else if ("xml" == e.toLowerCase()) {
            if ("undefined" == typeof X2JS)
                return void $.loadScript("https://cdnjs.cloudflare.com/ajax/libs/x2js/1.2.0/xml2json.min.js", function () {
                    convertToHtml(e)
                });
            var o = new X2JS;
            try {
                t = $.parseXML(t)
            } catch (e) {
                openErrorDialog("Invalid XML")
            }
            var n = o.xml2json(t);
            r = jsonTocsvbyjson(n, !0),
                toHTML(r, e)
        }
}
function csv2xml_json(e) {
    var t = editorAce.getValue();
    if (0 < t.trim().length)
        if ("xml" == e) {
            editorResult.getSession().setMode("ace/mode/xml");
            var r, o = CSV2JSON(t), n = (new XML.ObjTree).writeXML($.parseJSON(o));
            n = decodeSpecialCharacter(n);
            try {
                r = $.parseXML(n)
            } catch (e) {
                r = !1
            }
            0 == r && (n = n.substr(0, 39) + "<root>" + n.substr(39) + "</root>"),
                editorResult.setValue(vkbeautify.xml(n)),
                setOutputMsg("CSV TO XML")
        } else if ("json" == e) {
            editorResult.getSession().setMode("ace/mode/json");
            o = CSV2JSON(t);
            var l = vkbeautify.json(o);
            editorResult.setValue(l),
                setOutputMsg("CSV TO JSON")
        }
}
function defaultAction() { }
function htmlOutput(e, t) {
    var r = e;
    if (null != r && 0 < r.trim().length) {
        var o = document.getElementById("result1").contentWindow.document;
        if (null != t) {
            var n = "<!DOCTYPE html><html>\n";
            n = n + "<head><meta charset='UTF-8'><title>" + t.toUpperCase() + " To HTML using codebeautify.org</title></head>\n",
                n += "<body>\n",
                n += r,
                n += "\n</body>\n",
                n += "</html>",
                $("#" + t + "ToHtmlData").text(vkbeautify.xml(n)),
                void 0 !== editorResult && editorResult.setValue(vkbeautify.xml(n))
        }
        o.open(),
            o.write("<center>" + r + "</center>"),
            o.close(),
            $("html, body").animate({
                scrollTop: 0
            }, 10)
    }
}
function createXML_JSONFile() {
    var e = editorResult.getValue();
    if (0 < e.trim().length) {
        var t = new Blob(["" + e], {
            type: "text/plain;charset=utf-8"
        })
            , r = editorResult.getSession().getMode().$id;
        "ace/mode/xml" == r && fileDownloadCB(t, "codebeautify.xml"),
            "ace/mode/json" == r && fileDownloadCB(t, "codebeautify.json")
    } else
        openErrorDialog("Sorry Result is Empty")
}
function csvTosql(e) {
    var t = editorAce.getValue();
    try {
        if (null != t && 0 != t.trim().length) {
            var r = Papa.parse(t)
                , o = r.data.slice(1, t.length);
            o.sort(function (e, t) {
                return t.length - e.length
            }),
                0 == o.length && (o = r.data);
            var n = $("#tableName").val();
            0 == n.trim().length && (n = "TABLE_NAME");
            var l = createTableString(o[0], r.data, n);
            0 < (l += createQueryString(r.data, o[0].length, e, n)).trim().length && $.ajax({
                type: "post",
                url: globalurl + "Ql/formateQL",
                dataType: "text",
                data: {
                    data: l
                },
                success: function (e) {
                    try {
                        editorResult.setValue(e)
                    } catch (e) {
                        editorResult.setValue(l)
                    }
                },
                error: function (e, t, r) {
                    editorResult.setValue(l)
                }
            }),
                setOutputMsg("CSV TO SQL ")
        }
    } catch (e) {
        console.log(e),
            editorResult.setValue("Error To  Converting SQL")
    }
}
function isNumeric(e) {
    return e == Number(e) ? "number" : "string"
}
function createTableString(e, t, r) {
    var o = "/* CREATE TABLE */";
    o += "CREATE TABLE IF NOT EXISTS " + r + "(";
    for (var n = 0; n < e.length; n++) {
        var l = "";
        "number" == isNumeric(e[n]) && 0 != e[n].length && "" != e[n] ? l = 0 != n ? "DECIMAL(10,2)" : "INT(11)" : "string" == (l = typeof e[n]) && (l = "VARCHAR(100)"),
            n < t[0].length ? t[0][n] = t[0][n].replace(" ", "_") : t[0][n] = "COLUMN" + (n + 1),
            o += t[0][n] + " " + l + ",",
            n != e.length - 1 && (o += "\n")
    }
    return o = o.substring(0, o.length - 1),
        o += ");"
}
function createQueryString(e, t, r, o) {
    for (var n = e[0].join().replace(/"/g, "").replace(/'/g, ""), l = "", i = 1; i < e.length; i++) {
        var s = "";
        if ("insert" == r.toLowerCase() ? (s += "/* INSERT QUERY */",
            s += "INSERT INTO " + o + "(" + n + ") VALUES( ") : "update" == r.toLowerCase() ? (s += "/* UPDATE QUERY */",
                s += "UPDATE " + o + " SET ") : (s += "/* DELETE QUERY */",
                    s += "DELETE FROM " + o + " WHERE "),
            "delete" != r.toLowerCase()) {
            for (var a = 0; a < e[i].length; a++)
                a < t ? "number" == isNumeric(e[i][a]) && 0 != e[i][a].length ? "insert" == r.toLowerCase() ? s += e[i][a] + "," : s += e[0][a] + "=" + e[i][a] + "," : "insert" == r.toLowerCase() ? s += "'" + e[i][a].replace(/"/g, "") + "'," : s += e[0][a] + "='" + e[i][a].replace(/"/g, "") + "'," : "insert" == r.toLowerCase() ? s += "''," : s += "COLUMN" + (a + 1) + " = '',";
            if (s = s.substring(0, s.length - 1),
                "insert" == r.toLowerCase())
                s += ");\n";
            else if ("update" == r.toLowerCase()) {
                var c = "";
                c = "number" == isNumeric(e[i][0]) && 0 != e[i][0].length && "" != e[i][0] ? e[i][0] : "'" + e[i][0] + "'",
                    s += "  WHERE " + e[0][0] + "=" + c,
                    s += ";\n"
            }
        } else {
            c = "";
            c = "number" == isNumeric(e[i][0]) && 0 != e[i][0].length && "" != e[i][0] ? e[i][0] : "'" + e[i][0] + "'",
                s += e[0][0] + "=" + c,
                s += ";\n"
        }
        l += s
    }
    return l
}
function csvToMultiline() {
    var e = editorAce.getValue();
    try {
        if (null != e && 0 != e.trim().length) {
            for (var t = "", r = e.split("\n"), o = 1; o < r.length; o++) {
                for (var n = r[o].split(/,(?![^"]*"(?:(?:[^"]*"){2})*[^"]*$)/), l = 0; l < n.length; l++)
                    t += n[l].replace(/^"(.+)"$/, "$1") + "\n";
                t += "----",
                    t += "\n"
            }
            editorResult.setValue(t),
                setOutputMsg("CSV TO MUTLI LINE")
        }
    } catch (e) {
        console.log(e),
            editorResult.setValue("Error To  Converting Multi Line")
    }
}
$(document).ready(function () {
    "csv-to-json-converter" == (viewname = $("#viewName").val().trim()) ? (setViewTitle("CSV TO JSON", !0, !0),
        createEditor("text", "xml")) : "csv-to-html-converter" == viewname ? (setViewTitle("CSV TO HTML", !0, !0),
            createEditor("text", "html")) : "csv-to-xml-json" == viewname ? (setViewTitle("CSV TO XML/JSON", !0, !0),
                createEditor("text", "xml")) : "csv-to-sql-converter" == viewname ? (setViewTitle("CSV TO SQL Converter", !0, !0),
                    createEditor("text", "sql")) : "csv-to-xml-converter" == viewname ? (setViewTitle("CSV TO XML", !0, !0),
                        createEditor("text", "xml")) : "csv-to-multi-line-converter" == viewname ? (setViewTitle("CSV TO Multi Line Data Converter", !0, !0),
                            createEditor("text", "text")) : "xml-to-html-converter" == viewname ? (setViewTitle("XML TO HTML Converter", !0, !0),
                                createEditor("xml", "html")) : "json-to-html-converter" == viewname ? (setViewTitle("JSON TO HTML Converter", !0, !0),
                                    createEditor("json", "html")) : "csv-to-excel-converter" == viewname && (createEditor("csv"),
                                        setViewTitle("Online CSV TO EXCEL Converter", !0, !0))
});
