@groovy.transform.BaseScript com.ibm.dbb.groovy.ScriptLoader baseScript
import com.ibm.dbb.metadata.*
import com.ibm.dbb.dependency.*
import com.ibm.dbb.build.*
import groovy.json.JsonParserType
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.transform.*
import com.ibm.jzos.ZFile
import com.ibm.dbb.build.report.*
import com.ibm.dbb.build.report.records.*
import com.ibm.jzos.FileAttribute
import groovy.ant.*

//@Field def bindUtils= loadScript(new File("${props.zAppBuildDir}/utilities/BindUtilities.groovy"))
	
println("** Building files mapped to ${this.class.getName()}.groovy script")

println("***** Ray Lam testing Cobol.groovy")

def compileParms(String processorMember) {


/* Testing Processor Ray lam
	String ProcessorParm = props.getFileProperty('processorSearchPath', buildFile)
	println "***** Ray Lam Processor parms for $buildFile = $ProcessorParm"

	 println "***** Ray Lam call parseProcessor"
	String processor = parseProcessor(buildFile, logicalFile, member, logFile)
 

// Ray Lam Test Processor 
	String processorFile = props.getFileProperty('processorSearchPath', buildFile)

	println "***** Ray Lam Processor file for $buildFile = $processorFile"	
	String processorMember = "${processorFile}/${member}.pro"
	
	println "***** Ray Lam Processor member for $buildFile = $processorMember"	

*/
// Parse the JSON file

	println "***** Ray Lam Parsing JSON file using JsonSlurper"	
	String[] allowedEncodings = ["UTF-8", "IBM-1047"]
	//depFilePath = getAbsolutePath(processorMember)
	// Load dependency file and verify existance
	File depFile = new File(processorMember)
	assert depFile.exists() : "*! Dependency file not found: ${depFile}"

    
    String encoding = retrieveHFSFileEncoding(depFile) // Determine the encoding from filetag
	JsonSlurper slurper = new JsonSlurper().setType(JsonParserType.INDEX_OVERLAY) // Use INDEX_OVERLAY, fastest parser
	def depFileData
	if (encoding) {
		println "Parsing dependency file as ${encoding}: "
		assert allowedEncodings.contains(encoding) : "*! Dependency file must be encoded and tagged as either UTF-8 or IBM-1047 but was ${encoding}"
		depFileData = slurper.parse(depFile, encoding) // Parse dependency file with encoding
	}
	else {
		println "[WARNING] Dependency file is untagged. \nParsing dependency file with default system encoding: "
		depFileData = slurper.parse(depFile) // Assume default encoding for system
	}
	 println new JsonBuilder(depFileData).toPrettyString() // Pretty print if verbose
	

	return depFileData 

}
	
	def retrieveHFSFileEncoding(File file) {
	FileAttribute.Stat stat = FileAttribute.getStat(file.getAbsolutePath())
    FileAttribute.Tag tag = stat.getTag()
	int i = 0
	if (tag != null)
	{
  		char x = tag.getCodeCharacterSetID()
  		i = (int) x
	}

	switch(i) {
		case 0: return null // Return null if file is untagged
		case 1208: return "UTF-8"
		default: return "IBM-${i}"
	}
	
}