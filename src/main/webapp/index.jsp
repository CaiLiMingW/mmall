<html>
<body>
<h2>Hello World!</h2>
    <h3>SpringMVC文件上传</h3>
    <form name="from1" action="/manage/product/upload.do" method="post" enctype="multipart/form-data">
        <input type="file" name="upload_file"/>
        <input type="submit" value="上传"/>
    </form>
    <h3>富文本上传</h3>
    <form name="form2" action="/manage/product/richtext_img_upload.do" method="post"  enctype="multipart/form-data">
        <input type="file" name="upload_file">
        <input type="submit" value="upload"/>
    </form>
</body>
</html>
