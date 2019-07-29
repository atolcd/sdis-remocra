begin;

-- Ajout du modèle de mail pour la synchro des utilisateurs avec l'AD
INSERT INTO remocra.email_modele (code,corps,objet,version) VALUES ('SYNCHRO_LDAP','<title>Synchronisation des utilisateurs BSPP REMOCRA</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style type="text/css">
    div{width:800px;text-align:justify;}
    p{text-decoration:none;color:#000000;font-family: arial;font-size: 14px;}
    p.footer{text-decoration:none;font-style: italic;color:#AAAAAA;font-family: arial;font-size: 12px;}
    p.caution{text-decoration:none;font-style: italic;color:#000000;font-family: arial;font-size: 12px;}
    table{border-collapse:collapse;}
    td{text-decoration:none;border-width:1px;border-style:solid;color:#000000;font-family: arial;font-size: 14px;padding: 5px;}
</style>
<div>
    <p>Madame, Monsieur,<br/>
    <br />Des utilisateurs ont été ajoutés suite à la synchronisation automatique LDAP.<br/>
	<br />Vous retrouverez la liste de ces utilisateurs ci-dessous :
	<br />[UTILISATEURS_INSERES]</p>
    <p><a href="[URL_SITE]#admin/index/elt/utilisateurs">Gérer les utilisateurs</a><br/>
    <br/>Cordialement.</p>
    <p class="caution">En cas d''incompréhension de ce message, merci de prendre contact avec la BSPP.</p>
    <p class="footer">Ce message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.</p>
</div>','BSPP REMOCRA - Des utilisateurs ont été ajoutés',1);

commit;
