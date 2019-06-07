package common;

import java.util.HashMap;
import java.util.Map;

public class TreeNode {

    private String val;
    private Map<String, TreeNode> dirMap;
    private Map<String, FileNode> fileMap;

    public TreeNode(String val){
        this.val = val;
        this.dirMap = new HashMap<>();
        this.fileMap = new HashMap<>();
    }

    public String getVal(){
        return val;
    }

    public void addFileInCurrentDir(String val, FileNode file){
        fileMap.put(val, file);
    }

    public void addChild(String val){
        addChild(val, new TreeNode(val));
    }

    public void addChild(String val, TreeNode child){
        dirMap.put(val, child);
    }

    public TreeNode getChild(String val){
        return dirMap.get(val);
    }

    public boolean hasChild(String val){
        return dirMap.containsKey(val);
    }

    public TreeNode reachNode(String path){
        String[] pathList = path.split("/");
        TreeNode curNode = this;
        for(int i=0; i<pathList.length; i++){
            String step = pathList[i];
            if(!curNode.hasChild(step)){
                return null;
            }
            curNode = curNode.getChild(step);
        }
        return curNode;
    }

    public TreeNode addDescendantDir(String path){
        String[] pathList = path.split("/");
        TreeNode curNode = this;
        for(int i=0; i<pathList.length; i++){
            String step = pathList[i];
            if(curNode.hasChild(step)){
            }else{
                curNode.addChild(step);
            }
            curNode = curNode.getChild(step);
        }
        return curNode;
    }

    public void addFile(String absPath, FileNode file){
        int index = absPath.lastIndexOf("/");
        String dir = absPath.substring(0, index);
        String fileName = absPath.substring(index);
        TreeNode curDir = addDescendantDir(dir);
        curDir.addFileInCurrentDir(fileName, file);
    }

    public String listDir(String dirPath){
        TreeNode targetDir = reachNode(dirPath);
        StringBuilder sb = new StringBuilder();
        for(String dir : dirMap.keySet()){
            sb.append(dir);
            sb.append(";");
        }
        sb.append("dirFileDivider");
        for(String fileName : fileMap.keySet()){
            sb.append(fileName);
        }
        return sb.toString();
    }

    public String getFileKey(String fileName){
        return fileMap.get(fileName).getContent();
    }

}
