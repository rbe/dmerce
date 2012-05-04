/**
 * @author mm
 * @version $Id: GetFiles.java,v 1.1 2004/03/29 13:39:32 rb Exp $
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 * 
 */

public class GetFiles {
	
	public static void main(String[] args) {

		//GetFilesUrlConnect ftpDownload = new GetFilesUrlConnect();
		
		//String[] file = new String[1]; 
		
		//XmlFormChecker newChecker = new XmlFormChecker();
		//newChecker.check(file);
		long startTime = System.currentTimeMillis();
		
		//System.out.println("Aufruf von newChecker.check() erfolgreich!");
		long endTime = System.currentTimeMillis();
		long runTime = endTime - startTime;
		float runSeconds = ((float) runTime) / 1000.F;
		System.out.println("Run time was " + runSeconds + " seconds.");		

		//GetFilesInfo l = new GetFilesInfo();

		//l.setUrl("");

		//l.setHost("ftp.host.sk");
		//l.setPort(21);
		//l.setPath("pics");
		//l.setFilename("magni_main_bg.jpg");
		//l.setUsername("magni");
		//l.setPassword("jordan23");
		
		//ftpDownload.getServerContent(l);
		//GetFilesXmlParser.parse("test/serverliste.xml");
		
		//GetFilesXmlParser(l.getHost);
		//GetFilesXmlParserInfo l = new GetFilesXmlParserInfo();
		
		
		
		//ftpDownload.download(l);
	}
}

		//ftpDownload.download();

		/*String[] server =
			{
				"ftp://magni:jordan23@ftp.host.sk/pics/magni_main_bg.jpg",
				"http://www.magni.host.sk/pics/matrix2.jpg" 
			};
		for (int i = 0; i < 2; i++)
			System.out.println(server[i]);*/

		/*String line;
		String filename = "";
		File inputFile = new File(filename);
		try {
			BufferedReader in = new BufferedReader(new FileReader(inputFile));
		
			while ((line = in.readLine()) != null) {
				ftpDownload.setUrl(line);
				ftpDownload.download();
				//System.out.println(line);		
			}
			in.close();
		}
		catch (IOException e) {
			System.out.println(e);
		}*/

		//ftpDownload.download();


/*	public void downloadFtp() throws IOException {

		try {
			FtpClient ftpClient;
			System.out.println("Connecting to " + host);
			ftpClient = new FtpClient(host, port);
			try {
				ftpClient.login(username, pass);
			}
			catch (IOException e) {
				System.out.println("Login fehlgeschlagen");
			}

			ftpClient.binary();
			ftpClient.cd(path);

			TelnetInputStream listing = ftpClient.list();
			int c;

			while ((c = listing.read()) != -1) {
				System.out.print((char) c);
			}

			InputStream is = ftpClient.get(filename);
			BufferedInputStream bis = new BufferedInputStream(is);

			OutputStream os = new FileOutputStream(filename);
			BufferedOutputStream bos = new BufferedOutputStream(os);

			byte[] buffer = new byte[1024];
			int readCount;

			System.out.println("Getting: " + filename);

			while ((readCount = bis.read(buffer)) > 0) {
				bos.write(buffer, 0, readCount);
			}

			bos.close();
		}
		catch (StringIndexOutOfBoundsException e) {
			System.out.println(e);
		}
	}
}*/