# Application des patches de base de données

Les patches de remocra sont placés ici.

Les *patches communs* à tous les SDIS sont numérotés. Chaque patch suit le modèle qui suit, dans lequel on précise :

* le numéro de patch (`85` dans l'exemple)
* le message lié (`Fonction de calculInduispo` dans l'exemple)
* le contenu réel entre les commentaires

Exemple :

    begin;
    
    set statement_timeout = 0;
    set client_encoding = 'UTF8';
    set standard_conforming_strings = off;
    set check_function_bodies = false;
    set client_min_messages = warning;
    set escape_string_warning = off;
    
    set search_path = remocra, pdi, public, pg_catalog;
    
    --------------------------------------------------
    -- Versionnement du patch et vérification
    --
    create or replace function versionnement_dffd4df4df() returns void language plpgsql AS $body$
    declare
        numero_patch int;
        description_patch varchar;
    begin
        -- Métadonnées du patch
        numero_patch := 85;
        description_patch := 'Fonction de calculInduispo';
    
        -- Vérification
        if (select numero_patch-1 != (select max(numero) from remocra.suivi_patches)) then
            raise exception 'Le numéro de patch requis n''est pas le bon. Dernier appliqué : %, en cours : %', (select max(numero) from remocra.suivi_patches), numero_patch; end if;
        -- Suivi
        insert into remocra.suivi_patches(numero, description) values(numero_patch, description_patch);
    end $body$;
    select versionnement_dffd4df4df();
    drop function versionnement_dffd4df4df();
    
    --------------------------------------------------
    -- Contenu réel du patch début
    --
    
    
    -- Contenu réel du patch fin
    --------------------------------------------------
    
    commit;

Les *patches spécifiques à un SDIS et liés à un patch commun* sont placés dans un répertoire propre au SDIS. Le nom du patch porte le numéro de patch et le code du SDIS : `patches/<NUMERO_PATCH>/<CODE_SDIS>/<NUMERO_PATCH>_<CODE_SDIS>.sql`

Exemple pour le patch 069 :

 * `patches/069/069.sql` : patch commun
 * `patches/069/83/069_83.sql` : patch propre au SDIS83


Les *patches complémentaires spécifiques aux SDIS et non liés à des patches communs* sont placés dans le répertoire `sdis/<CODE_SDIS>`.
Le nom du patch porte le numéro du patch commun courant lors de sa création du patch et un numéro global propre au SDIS : `<NUMERO_PATCH_COMMUN>_<NUMERO_D_ORDRE>.sql`

Exemple pour le premier patch qui suit cette règle :

* `77/069_001.sql` : Premier patch appliqué spécifique au SDIS 77 non lié à un patch commun, créé lorsque le dernier patch commun était le 69ème.


