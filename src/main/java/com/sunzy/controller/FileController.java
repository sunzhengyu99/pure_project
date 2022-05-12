package com.sunzy.controller;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunzy.common.R;
import com.sunzy.entity.Files;
import com.sunzy.mapper.FileMapper;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * 文件上传
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${files.upload.path}")
    private String path;


    @Autowired
    private FileMapper fileMapper;

    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extName = FileUtil.extName(originalFilename);

        long size = file.getSize();
        // 文件唯一表示码u

        String uuid = IdUtil.fastSimpleUUID();
        String fileName = uuid + StrUtil.DOT + extName;
        File uploadFile = new File(path + fileName);
        // 获取文件保存的上一级目录
        File parentFile = uploadFile.getParentFile();
        // 判断该目录是否存在 不存在则创建该目录
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        String url = "http://127.0.0.1:81/";
        String md5 = SecureUtil.md5(file.getInputStream());
        Files dbFile = getFieldMd5(md5);
        if (dbFile == null) {
            // 将临时文件转存到磁盘中
            file.transferTo(uploadFile);
            // 数据库若不存在重复文件，则不删除刚才上传的文件
            url = "http://localhost:81/file/" + fileName;

            Files files = new Files();
            files.setName(originalFilename);
            files.setType(extName);
            files.setSize(size / 1024);
            files.setUrl(url);
            files.setMd5(md5);
            fileMapper.insert(files);
        } else {
            url = dbFile.getUrl();
        }

        return url;
    }


    /**
     * 文件下载
     *
     * @param fileName
     * @param response
     */
    @GetMapping("/{fileName}")
    public void download(@PathVariable String fileName, HttpServletResponse response) throws IOException {
        File file = new File(path + fileName);
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        response.setContentType("application/octet-stream");

        // 读取文件的字节流

        outputStream.write(FileUtil.readBytes(file));
        outputStream.flush();
        outputStream.close();
//        os.write(FileUtil.readBytes(uploadFile));
//        os.flush();
//        os.close();

    }


    /**
     * 更新文件信息
     *
     * @param file
     * @return
     */
    public R update(@RequestBody Files file) {
        return R.success(fileMapper.updateById(file));
    }

    /**
     * 删除文件（只是将数据库中的isdelete字段更新）
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Integer id) {
        Files files = fileMapper.selectById(id);
        files.setIsDelete(true);
        fileMapper.updateById(files);
        return R.success();
    }

    @PostMapping("/del/batch")
    public R deleteByIds(@RequestBody List<Integer> ids) {
        LambdaQueryWrapper<Files> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Files::getId, ids);
        // 查询到所有的记录
        List<Files> files = fileMapper.selectList(queryWrapper);
        for (Files file : files) {
            file.setIsDelete(true);
            fileMapper.updateById(file);
        }
        return R.success();
    }


    /**
     * 文件的分页查询
     *
     * @param pageNum
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R page(@RequestParam int pageNum, @RequestParam int pageSize,
                  @RequestParam(defaultValue = "") String name) {
        Page<Files> filesPage = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Files> qw = new LambdaQueryWrapper<>();
        qw.eq(Files::getIsDelete, false);
        qw.like(name != null, Files::getName, name);
        qw.orderByDesc(Files::getName);

        fileMapper.selectPage(filesPage, qw);
        return R.success(filesPage);
    }


    /**
     * 根据md5值查找文件
     *
     * @param md5
     * @return
     */
    private Files getFieldMd5(String md5) {
        LambdaQueryWrapper<Files> qw = new LambdaQueryWrapper<>();
        qw.eq(md5 != null, Files::getMd5, md5);
        List<Files> files = fileMapper.selectList(qw);
        return files.size() == 0 ? null : files.get(0);
    }


}
