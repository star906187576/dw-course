package com.dw.prepare

import java.io.{File, PrintWriter}
import java.sql.Date
import java.text.SimpleDateFormat

import scala.collection.mutable
import scala.collection.mutable.HashMap
import scala.io.Source

object InitSQLGen {
  def main(args: Array[String]): Unit = {

    val df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    //1.genersrt
    //1.1读取数据文件，处理并封装样例类（电影id和电影类型）
    val genres = Source.fromFile("D:\\App\\IDEA\\data\\dw-course\\batch-etl\\src\\main\\resources\\ml-100k\\ml-100k\\u.genre").getLines().map(line => {
      val tmp: Array[String] = line.split("\\|")
      Genre(tmp(1).toInt, tmp(0))
    })

    //1.2向文件写入sql语句
    val genreWriter = new PrintWriter(new File("D:\\App\\IDEA\\data\\dw-course\\batch-etl\\src\\main\\resources\\sql\\genres_init.sql"))
    genreWriter.println("USE movie;")
    genres.foreach(g => {
      genreWriter.println(s"""insert into genre values(${g.id}, "${g.name}");""")
    })
    genreWriter.close()

    //2.occupation
    //2.1读取数据文件，将数据（影评人职业）和对应下标存入hashmap以及样例类
    val occupationIdMap: mutable.HashMap[String, Int] = HashMap[String, Int]()
    val occupations = Source.fromFile("D:\\App\\IDEA\\data\\dw-course\\batch-etl\\src\\main\\resources\\ml-100k\\ml-100k\\u.occupation")
      //zipWithIndex，将数据和下标进行组合
      .getLines().zipWithIndex.map {
      case (name, index) =>
        occupationIdMap += (name -> index)
        Occupation(index, name)
    }

    //2.2向文件写入sql语句
    val occupationWriter = new PrintWriter(new File("D:\\App\\IDEA\\data\\dw-course\\batch-etl\\src\\main\\resources\\sql\\occupation_init.sql"))
    occupationWriter.println("use movie;")
    occupations.foreach(o =>
      occupationWriter.println(s"""insert into occupation values(${o.id},"${o.name}");""")
    )
    occupationWriter.close()

    //3.movies
    val moviesInfo: Iterator[(Movie, Array[MovieGenre])] = Source.fromFile("D:\\App\\IDEA\\data\\dw-course\\batch-etl\\src\\main\\resources\\ml-100k\\ml-100k\\u.item")
      .getLines().map(
      line => {
        val arr: Array[String] = line.split("\\|")
        //takeRight，获取数组后几位元素
        val movieIdgenreIds: Array[MovieGenre] = arr.takeRight(19).zipWithIndex.flatMap {
          case (ge, index) =>
            if (ge.toInt == 1) {
              Some(index)
            } else None
        }.map(MovieGenre(arr(0).toInt, _))
        (Movie(arr(0).toInt, arr(1), arr(2), arr(3), arr(4)), movieIdgenreIds)
      }
    )
    val moviesWriter = new PrintWriter(new File("D:\\App\\IDEA\\data\\dw-course\\batch-etl\\src\\main\\resources\\sql\\movies_init.sql"))
    moviesWriter.println("use movie;")
    val movieGenresWriter = new PrintWriter(new File("D:\\App\\IDEA\\data\\dw-course\\batch-etl\\src\\main\\resources\\sql\\movieGenres_init.sql"))
    movieGenresWriter.println("USE movie;")
    moviesInfo.foreach {
      case (m, mgs) => {
        val videoReleaseData = if (m.videoReleaseData.trim.length == 0) null else s"""STR_TO_DATE("${m.videoReleaseData}","%d-%M-%y")"""
        val releaseData = if (m.releaseData.trim.length == 0) null else s"""STR_TO_DATE("${m.releaseData}", "%d-%M-%Y")"""
        moviesWriter.println(s"""insert into movie values(${m.id},"${m.name}",${releaseData},${videoReleaseData},"${m.imdbUrl}");""".stripMargin)
        mgs.foreach {
          mg => movieGenresWriter.println(s"""insert into movie_genre values(${mg.movieId},${mg.genreId});""")
        }
      }
    }
    moviesWriter.close()
    movieGenresWriter.close()

    //4.user
    val userWriter = new PrintWriter(new File("D:\\App\\IDEA\\data\\dw-course\\batch-etl\\src\\main\\resources\\sql\\users_init.sql"))
    userWriter.println("use movie;")
    Source.fromFile("D:\\App\\IDEA\\data\\dw-course\\batch-etl\\src\\main\\resources\\ml-100k\\ml-100k\\u.user").getLines().foreach(line => {
      val tmp = line.split("\\|")
      userWriter.println(s"""insert into user values(${tmp(0).toInt}, '${tmp(2)}', "${tmp(4)}", ${tmp(1).toInt}, ${occupationIdMap.getOrElse(tmp(3), -1)}, NOW());""")
    })
    userWriter.close()

    //5.data
    val ratingsWriter = new PrintWriter(new File("D:\\App\\IDEA\\data\\dw-course\\batch-etl\\src\\main\\resources\\sql\\ratings_init.sql"))
    ratingsWriter.println("use movie;")
    Source.fromFile("D:\\App\\IDEA\\data\\dw-course\\batch-etl\\src\\main\\resources\\ml-100k\\ml-100k\\u.data").getLines().zipWithIndex.foreach { case (line, index) =>
      val tmp = line.split("\t")
      val ts = tmp(3).toLong
      val dt = s"""STR_TO_DATE("${df.format(new Date(ts))}", "%Y-%m-%d %T")"""
      ratingsWriter.println(s"""insert into user_rating values(${index}, ${tmp(0).toInt}, ${tmp(1).toInt}, ${tmp(2).toInt}, ${dt});""")
    }
    ratingsWriter.close()
  }
}

case class Genre(id: Int, name: String)

case class Occupation(id: Int, name: String)

case class Movie(id: Int, name: String, releaseData: String, videoReleaseData: String, imdbUrl: String)

case class MovieGenre(movieId: Int, genreId: Int)
