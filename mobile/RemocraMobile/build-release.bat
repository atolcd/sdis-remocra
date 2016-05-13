@rem 
@rem   Construit l'apk en mode release
@rem 

@rem Déplacement dans le répertoire du script
cd %~dp0

@rem Lancement mode release
../gradlew.bat assembleRelease

