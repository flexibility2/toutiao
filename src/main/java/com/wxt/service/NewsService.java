package com.wxt.service;

import com.wxt.dao.NewsDAO;
import com.wxt.model.News;
import com.wxt.utils.TouTiaoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class NewsService {

    @Autowired
    private NewsDAO newsDAO;

    public List<News> getLatestNews(int userId, int offset, int limit)
    {
        return newsDAO.selectByUserIdAndOffset(userId,offset,limit);
    }


    public String saveImage(MultipartFile file) throws IOException {
        int dotPos = file.getOriginalFilename().lastIndexOf(".");
        if (dotPos<=0)
        {
            return null;
        }

        String fileExt = file.getOriginalFilename().substring(dotPos+1);
        if (TouTiaoUtils.isFileAllowed(fileExt)==false)
        {
            return null;
        }

        String fileName = UUID.randomUUID().toString().replace("-","") + "." + fileExt;

        Files.copy(file.getInputStream(),new File(TouTiaoUtils.IMG_DIR + fileName).toPath(),  StandardCopyOption.REPLACE_EXISTING);

        return TouTiaoUtils.TOUTIAO_DOMAIN + "image?name=" + fileName;

    }

    public void addNews(News news)
    {
        newsDAO.addNews(news);
    }

    public News selectById(int newsId)
    {
        return newsDAO.getById(newsId);
    }

    public int updateCommentCount(int id, int count) {
        return newsDAO.updateCommentCount(id, count);
    }

    public int updateLikeCount(int id,int count)
    {
        return newsDAO.updateLikeCount(id,count);
    }

}
