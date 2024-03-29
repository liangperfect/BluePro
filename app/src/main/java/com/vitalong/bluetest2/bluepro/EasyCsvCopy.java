package com.vitalong.bluetest2.bluepro;

import android.app.Activity;
import android.os.Environment;

import net.ozaydin.serkan.easy_csv.FileCallback;
import net.ozaydin.serkan.easy_csv.Utility.PermissionUtility;
import net.ozaydin.serkan.easy_csv.Utility.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class EasyCsvCopy {
    private Activity activity;
    private File file;
    private OutputStream outputStream;
    private String separatorColumn = ",";
    private String seperatorLine = "\r\n";

    public EasyCsvCopy(Activity activity) {
        this.activity = activity;
    }

    public void createCsvFile(String fileName, List<String> headerList, List<String> data, int permissionRequestCode, FileCallback fileCallback) {
        if (PermissionUtility.askPermissionForActivity(this.activity, "android.permission.WRITE_EXTERNAL_STORAGE", permissionRequestCode)) {
            fileName = fileName.replace(" ", "_").replace(":", "_");
            this.file = new File(Environment.getExternalStorageDirectory() + File.separator + fileName + ".csv");
            this.outputStream = null;

            try {
                this.file.createNewFile();
            } catch (IOException var8) {
                fileCallback.onFail(var8.getMessage());
            }

            List<String> headerListWithComma = Utils.separatorReplace(this.separatorColumn, this.seperatorLine, headerList);
            List<String> dataListWithComma = Utils.separatorReplace(this.separatorColumn, this.seperatorLine, data);
            this.file = this.writeDataToFile(this.file, this.containAllData(headerListWithComma, dataListWithComma), fileCallback);
            fileCallback.onSuccess(this.file);
        } else {
            fileCallback.onFail("Write Permission Error");
        }
    }

    private File writeDataToFile(File file, List<String> dataList, final FileCallback fileCallback) {
        if (file.exists()) {
            try {
                this.outputStream = new FileOutputStream(file);
            } catch (FileNotFoundException var6) {
                fileCallback.onFail(var6.getMessage());
            }

            final OutputStream finalFo = this.outputStream;
            byte [] bs = { (byte)0xEF, (byte)0xBB, (byte)0xBF};  //UTF-8编码
            try {
                finalFo.write(bs);//excel打开csv文件必须添加bom的一个头部信息，打开才不会乱码
            } catch (IOException e) {
                e.printStackTrace();
            }
            String[] headerArray = new String[dataList.size() - 1];
            headerArray = (String[]) dataList.toArray(headerArray);
            Observable.fromArray(headerArray).subscribe(new Observer() {
                public void onSubscribe(Disposable d) {
                }

                public void onNext(Object o) {
                    String dataWithLineBreak = (String) o;

                    try {
                        //UnicodeBigUnmarked    UTF-8   ISO8859-1
//                        dataWithLineBreak = new String(dataWithLineBreak.getBytes("ISO-8859-1"), "GBK");
//                        Log.d("chenliang","datawithline数据->"+dataWithLineBreak);
//                        finalFo.write(dataWithLineBreak.getBytes("gb2312"));
                        finalFo.write(dataWithLineBreak.getBytes());
//                        finalFo.write(dataWithLineBreak.getBytes("UTF-8"));
//                        finalFo.write(dataWithLineBreak.getBytes("UnicodeBigUnmarked"));
//                        finalFo.write(dataWithLineBreak.getBytes("unicode"));//   uo
//                        finalFo.write(dataWithLineBreak.getBytes("ISO8859-1"));//iso
                    } catch (IOException var4) {
                        fileCallback.onFail(var4.getMessage());
                    }
                }

                public void onError(Throwable e) {
                    fileCallback.onFail(e.getMessage());
                }

                public void onComplete() {
                    try {
                        finalFo.close();
                    } catch (IOException var2) {
                        var2.printStackTrace();
                    }
                }
            });
            return file;
        } else {
            fileCallback.onFail("Couldn't create CSV file");
            return file;
        }
    }

    private List<String> containAllData(List<String> headerList, List<String> dataList) {
        List<String> stringList = new ArrayList();
        Iterator var4 = headerList.iterator();

        String value;
        while (var4.hasNext()) {
            value = (String) var4.next();
            stringList.add(value);
        }

        var4 = dataList.iterator();

        while (var4.hasNext()) {
            value = (String) var4.next();
            stringList.add(value);
        }

        return stringList;
    }

    public void setSeparatorColumn(String separatorColumn) {
        this.separatorColumn = separatorColumn;
    }

    public void setSeperatorLine(String seperatorLine) {
        this.seperatorLine = seperatorLine;
    }
}
