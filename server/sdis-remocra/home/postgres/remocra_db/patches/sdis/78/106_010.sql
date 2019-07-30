begin;

-- Mise à jour du modèle de mail pour les PEI aillant changés d'état de dispo
UPDATE remocra.email_modele SET corps = '<title>Rapport des bascules des dipos des PEI</title><meta http-equiv="Content-Type" content="text/html; charset=UTF-8"><style type="text/css">div{width:800px;text-align:justify;}p{text-decoration:none;color:#000000;font-family: arial;font-size: 14px;}p.footer{text-decoration:none;font-style: italic;color:#AAAAAA;font-family: arial;font-size: 12px;}p.caution{text-decoration:none;font-style: italic;color:#000000;font-family: arial;font-size: 12px;}table{border-collapse:collapse;}td{text-decoration:none;border-width:1px;border-style:solid;color:#000000;font-family: arial;font-size: 14px;}</style><div><p>Bonjour,<br/><br/>Des PEI ont changé d''état de disponibilité durant les dernières [INTERVAL] heures.<br/><br/>Vous pouvez retrouver ces informations sur la plateforme Remocra: <a href="[URL_SITE]">[URL_SITE]</a>[TABLE]<br/>Cordialement.</p><p class="caution">En cas d''incompréhension de ce message, merci de prendre contact avec le SDIS.</p><p class="footer">Ce message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.</p></div>'
WHERE code = 'BASCULE_DISPO';

commit;
