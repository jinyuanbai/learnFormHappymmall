<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<body>
<h2>Hello World!</h2>

springmvc上传文件fsdfds
<form name="form1" action="/manage/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="uploadFile"/>
    <input type="submit" value="springmvc上传文件"/>
</form>

<br>

富文本图片上传文件
<form name="form2" action="/manage/product/richtextImgUpload.do" method="post"
      enctype="multipart/form-data">
    <input type="file" name="uploadFile"/>
    <<input type="submit" value="富文本图片上传文件"/>
</form>
</body>
</html>
