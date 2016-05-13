Ext.require('Ext.app.Application');

// Ces singletons sont utilisés dans les config de certaines classes donc on les charges ici pour être
// sur qu'ils ont bien été chargé.
Ext.require('Sdis.Remocra.util.Util');
  
Ext.ns('Sdis.Remocra');
Ext.application({
    name: 'Sdis.Remocra',
    
//        controllers: [
//            'Users'
//        ],

        launch: function() {
            //include the tests in the test.html head
            jasmine.getEnv().addReporter(new jasmine.TrivialReporter());
            jasmine.getEnv().execute();
        }
});