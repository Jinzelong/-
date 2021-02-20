package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class Controller {
    private String aaptPath;

    //储存信息
    private List<Map<String,String>> userList1 = new ArrayList<Map<String,String>>();

    @FXML
    public TableView tableview;
    @FXML
    public TableColumn company;
    @FXML
    public TableColumn demo;
    @FXML
    public TableColumn targetSdkVersion;
    @FXML
    public TableColumn MD5;
    @FXML
    public TableColumn versionCode;
    @FXML
    public TableColumn InstallLocation;
    @FXML
    public TableColumn versionName;

    @FXML
    public Button output;
    @FXML
    public Button clear;

    private String[] arrayList ;
    private String[] arrayList2;

    private ObservableList<apk> data;

    public static void createExcelFileBySuffix(String excelSuffixType,String fileDir, List<String> sheetNames, String titleRow[]){

        Workbook workbook=null;
        //创建workbook
        if(".xls".equals(excelSuffixType)){
            workbook = new HSSFWorkbook();
        }else if(".xlsx".equals(excelSuffixType)){
            workbook = new XSSFWorkbook();
        }
        //新建文件
        FileOutputStream fileOutputStream = null;
        Row row = null;
        try {
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
            cellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
            //添加Worksheet（不添加sheet时生成的xls文件打开时会报错)
            for(int i = 0; i<sheetNames.size(); i++){
                workbook.createSheet(sheetNames.get(i));
                workbook.getSheet(sheetNames.get(i)).createRow(0);
                //添加表头, 创建第一行
                row = workbook.getSheet(sheetNames.get(i)).createRow(0);
                row.setHeight((short)(20*20));
                for (short j = 0; j < titleRow.length; j++) {
                    Cell cell = row.createCell(j, CellType.BLANK);
                    cell.setCellValue(titleRow[j]);
                    cell.setCellStyle(cellStyle);
                }
                fileOutputStream = new FileOutputStream(fileDir);
                workbook.write(fileOutputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void writeToExcelFileBySuffix(String excelSuffixType,String fileDir, String sheetName, List<Map<String,String>> mapList) throws Exception{
        //创建workbook
        File file = new File(fileDir);
        Workbook workbook=null;
        try {
            //创建workbook
            if(".xls".equals(excelSuffixType)){
                workbook = new HSSFWorkbook(new FileInputStream(file));
            }else if(".xlsx".equals(excelSuffixType)){
                workbook = new XSSFWorkbook(new FileInputStream(file));
            }
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        //文件流
        FileOutputStream fileOutputStream = null;
        Sheet sheet = workbook.getSheet(sheetName);
        // 获取表格的总行数
        // int rowCount = sheet.getLastRowNum() + 1; // 需要加一
        //获取表头的列数
        int columnCount = sheet.getRow(0).getLastCellNum();
        try {
            // 获得表头行对象
            Row titleRow = sheet.getRow(0);
            //创建单元格显示样式
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
            cellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
            if(titleRow!=null){
                for(int rowId = 0; rowId < mapList.size(); rowId++){
                    Map<String,String> map = mapList.get(rowId);
                    Row newRow=sheet.createRow(rowId+1);
                    newRow.setHeight((short)(20*20));//设置行高  基数为20
                    for (short columnIndex = 0; columnIndex < columnCount; columnIndex++) {  //遍历表头
                        //trim()的方法是删除字符串中首尾的空格
                        String mapKey = titleRow.getCell(columnIndex).toString().trim();
                        Cell cell = newRow.createCell(columnIndex);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(map.get(mapKey)==null ? null : map.get(mapKey).toString());
                    }
                }
            }
            fileOutputStream = new FileOutputStream(fileDir);
            workbook.write(fileOutputStream);
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




    public void Init(){
        company.setCellValueFactory(new PropertyValueFactory<>("name"));
        demo.setCellValueFactory(new PropertyValueFactory<>("packagename"));
        targetSdkVersion.setCellValueFactory(new PropertyValueFactory<>("targetVersion"));
        MD5.setCellValueFactory(new PropertyValueFactory<>("MD5"));
        versionCode.setCellValueFactory(new PropertyValueFactory<>("versionCode"));
        versionName.setCellValueFactory(new PropertyValueFactory<>("versionName"));
        InstallLocation.setCellValueFactory(new PropertyValueFactory<>("install"));
        output.setVisible(false);
        clear.setVisible(false);
        data = FXCollections.observableArrayList();
    }

    @FXML
    public void input_file(ActionEvent actionEvent){
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        Stage stage = (Stage)tableview.getScene().getWindow();
        List<File> fileList = fileChooser.showOpenMultipleDialog(stage);
        if (fileList != null){
            output.setVisible(true);
            clear.setVisible(true);
            fileList.stream().forEach((file -> {
        arrayList = cmdCommand(file.getAbsolutePath()).split("=");
        String regEx="[\n']";
        String aa = "";
        arrayList2 = cmdCommand(file.getAbsolutePath()).split("\n");
        String packagename = arrayList[1].split(" ")[0].replaceAll(regEx,aa);
        String name = packagename.split("\\.")[3];
        String versionname = arrayList[3].split(" ")[0].replaceAll(regEx,aa);
        String versionCode = arrayList[2].split(" ")[0].replaceAll(regEx,aa);
        String install = arrayList2[1].substring(18).replaceAll(regEx,aa);

        String targetVersion = arrayList2[3].substring(18,20);
        String md5 = getMD5(file.getAbsolutePath());
        data.add(new apk(companyname(name),packagename,targetVersion,md5,versionCode,versionname,install));
        tableview.setItems(data);
            Map<String,String> map=new HashMap<String,String>();
              map.put("渠道名称", companyname(name));
              map.put("package",packagename);
              map.put("targetVersion",targetVersion);
              map.put("MD5",md5);
              map.put("versionCode",versionCode);
              map.put("versionName",versionname);
              map.put("InstallLocation",install);
              userList1.add(map);
            }));

        }
    }
    @FXML
    public void clearAll(ActionEvent actionEvent){
        userList1.clear();
        data.clear();
        tableview.setItems(data);
        output.setVisible(false);
        clear.setVisible(false);
    }

    public String companyname(String name){
        switch (name){
            case "jh":{
                return "游戏fan-应用宝";
            }
            case "mi":{
                return "小米";
            }
            case "huawei":{
                return "华为";
            }
            case "gamecenter":{
                return "oppo";
            }
            case "nearme":{
                return "oppo";
            }
            case "vivo":{
                return "vivo";
            }
            case "mz":{
                return "魅族";
            }
            case "am":{
                return "金立";
            }
            case "lenovo":{
                return "联想";
            }
            case "bilibili":{
                return "B站";
            }
            case "m4399":{
                return "4399";
            }
            case "coolpad":{
                return "酷派";
            }
            case "nubia":{
                return "努比亚";
            }
            case "douyu":{
                return "斗鱼";
            }
            case "one360":{
                return "360";
            }
            case "baidu":{
                return "百度";
            }
            case "kuaishou":{
                return "快手";
            }
            case "samsung":{
                return "三星";
            }
            case "laohu":{
                return "老虎";
            }
            case "hykb":{
                return "好游快爆";
            }
            case "dangle":{
                return "当乐";
            }
            case "taptap":{
                return "TapTap";
            }
            case "uc":{
                return "九游";
            }
            case "mzw":{
                return "拇指玩";
            }
            case "iqiyi": {
                return "爱奇艺";
            }
            case "pptv":{
                return "PPTV";
            }
            case "papau":{
                return "啪啪游";
            }
            case "ewan":{
                return "益玩";
            }
            case "tt":{
                return "TT";
            }
            case "guopan":{
                return "果盘";
            }
            case "k57":{
                return "57K";
            }
            case "nubia3":{
                return "努比亚";
            }
            case "sogou":{
                return "搜狗";
            }

            default:{
                return name;
            }
        }
    }


    @FXML
    public void excel(ActionEvent actionEvent) throws IOException {
        File directory = new File("");

        SimpleDateFormat df = new SimpleDateFormat("YYYYMMDDhhmmss");
        String date = df.format(new Date());
        String excelSuffix=".xls";
        String fileDir ;
        fileDir = directory.getAbsolutePath()+"\\" + date + ".xls";


        List<String> sheetName = new ArrayList<>();

        sheetName.add("package检测");


        System.out.println(sheetName);

        String[] title = {"渠道名称","package","targetVersion","MD5","versionCode","versionName","InstallLocation"};
        createExcelFileBySuffix(excelSuffix,fileDir, sheetName, title);

        Map<String, List<Map<String, String>>> users = new HashMap<>();

        users.put("package检测",userList1);
        //删除List 集合中特定的元素
        for(Iterator<String> sheeNameIterator = sheetName.iterator();sheeNameIterator.hasNext();){

            String sheet = sheeNameIterator.next();
            System.out.println(users);
            if ( users.get(sheet).size() == 0) {

                sheeNameIterator.remove();

            }
        }

        System.out.println(sheetName.size());

        createExcelFileBySuffix(excelSuffix,fileDir, sheetName, title);
        for (int j = 0; j < sheetName.size(); j++) {

            try {
                writeToExcelFileBySuffix(excelSuffix,fileDir, sheetName.get(j), users.get(sheetName.get(j)));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        TextArea textArea = new TextArea("文件已保存至"+fileDir);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxHeight(50);
        GridPane gridPane = new GridPane();
        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(textArea, 0, 0);

        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.titleProperty().set("提示");
        alert.getDialogPane().setContent(gridPane);
        alert.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        alert.show();
    }

    private static void configureFileChooser(
            final FileChooser fileChooser) {
        fileChooser.setTitle("选择apk");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("APK安装包", "*.apk"));
    }

    public static String cmdCommand(String apkPath) {
        boolean flag = true;
        StringBuffer sb = new StringBuffer();
        File directory = new File("");
        String command = directory.getAbsolutePath()+"/aapt.exe" + " dump badging " + apkPath;
        BufferedReader br = null;
        try {
            Process p = Runtime.getRuntime().exec(command);
            InputStream is = p.getInputStream() != null ? p.getInputStream() : p.getErrorStream();
            br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String line = null;

            for (int i = 0; i < 40; i++) {
                line = br.readLine();
                sb.append(line + "\r\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }


    public static String getMD5(String path) {
                 BigInteger bi = null;
                 try {
                         byte[] buffer = new byte[8192];
                         int len = 0;
                         MessageDigest md = MessageDigest.getInstance("MD5");
                         File f = new File(path);
                         FileInputStream fis = new FileInputStream(f);
                         while ((len = fis.read(buffer)) != -1) {
                                 md.update(buffer, 0, len);
                             }
                         fis.close();
                         byte[] b = md.digest();
                         bi = new BigInteger(1, b);
                     } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                         e.printStackTrace();
                     }
                 return bi.toString(16);
             }
}
