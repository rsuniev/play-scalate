# Scala
import sys,os,inspect
	
# ~~~~~~~~~~~~~~~~~~~~~~ New
if play_command == 'new':
    module_dir = inspect.getfile(inspect.currentframe()).replace("commands.py","")
    shutil.copyfile(os.path.join(module_dir,'resources/Application.scala'), os.path.join(application_path, 'app/controllers/Application.scala'))
    shutil.copyfile(os.path.join(module_dir,'resources/index.ssp'), os.path.join(application_path, 'app/views/Application/index.ssp'))
    f = open(os.path.join(application_path, 'conf/application.conf'),'a')
    f.write('\n\n#scalate config\nscalate=ssp\njvm.memory=-Xmx256M,-Xms32M')
    f.close()
    os.remove(os.path.join(application_path, 'app/views/Application/index.html'))
    os.remove(os.path.join(application_path, 'app/views/main.html'))

# ~~~~~~~~~~~~~~~~~~~~~~ Eclipsify
if play_command == 'ec' or play_command == 'eclipsify':
    dotProject = os.path.join(application_path, '.project')
    replaceAll(dotProject, r'org\.eclipse\.jdt\.core\.javabuilder', "ch.epfl.lamp.sdt.core.scalabuilder")
    replaceAll(dotProject, r'<natures>', "<natures>\n\t\t<nature>ch.epfl.lamp.sdt.core.scalanature</nature>")
