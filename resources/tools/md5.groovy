import java.security.*
import org.dykman.gossamer.util.*

println encrypt(args[0],args[1])

def encrypt(u,p) {
  def digest = MessageDigest.getInstance('MD5')
  digest.update(u.getBytes())
  digest.update(":::".toString().getBytes())
  digest.update(p.getBytes())
  FileUtils.toHex(digest.digest())
}

