from drat.analysis import Checktext
import glob
import os
txts = glob.glob('/Users/chengyey/Workspace/dbpedia/abstracts/*.txt')
print("Subject,DC Index,CL Index,Uncommon Words,Unique Words")
for fname in txts:
	with open(fname) as f:
		data = f.read()
	subject = os.path.basename(fname).replace("_", " ").replace(".txt","")
	#print (subject)
	check = Checktext(subject, None, False, False)
	dc_score, cli_score = check.run_check(data.lower())
	print ('"' + subject +'"' + "," + str(dc_score) + "," + str(cli_score) + "," + str(check.uncom_len) + "," + str(check.uniq_len))

