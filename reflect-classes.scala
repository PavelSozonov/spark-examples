import scala.reflect.runtime.universe._
import scala.collection.JavaConverters._
import java.net.URLClassLoader

def getAllClasses: Seq[String] = {
  val classLoader = getClass.getClassLoader.asInstanceOf[URLClassLoader]
  val urls = classLoader.getURLs
  val classes = scala.collection.mutable.Set[String]()

  for (url <- urls) {
    val file = new java.io.File(url.getFile)
    if (file.isDirectory) {
      val files = file.listFiles
      for (f <- files if f.getName.endsWith(".class")) {
        val className = f.getPath
          .replace(file.getPath, "")
          .replace("/", ".")
          .replace("\\", ".")
          .replace(".class", "")
          .stripPrefix(".")
        classes += className
      }
    } else if (file.getName.endsWith(".jar")) {
      val jar = new java.util.jar.JarFile(file)
      val entries = jar.entries.asScala
      for (entry <- entries if entry.getName.endsWith(".class")) {
        val className = entry.getName
          .replace("/", ".")
          .replace("\\", ".")
          .replace(".class", "")
        classes += className
      }
    }
  }

  classes.toSeq
}

val allClasses = getAllClasses

allClasses.foreach { className =>
  try {
    val classLoader = getClass.getClassLoader
    val classFile = classLoader.getResource(className.replace('.', '/') + ".class")
    if (classFile != null) {
      println(s"Class: $className, File: $classFile")
    } else {
      println(s"Class: $className, File: Not found")
    }
  } catch {
    case e: Exception => println(s"Class: $className, Error: ${e.getMessage}")
  }
}
