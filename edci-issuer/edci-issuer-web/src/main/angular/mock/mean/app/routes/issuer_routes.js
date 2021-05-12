module.exports = function (app, db) {
  db.then(db => {

    app.get('/api/certificates', (req, res) => {
      res.send({ valid: true, credentials: db.get('certificates')});
    });

    app.post('/api/certificates/sign', (req, res) => {
      let credentials = req.body;

      for(let i = 0; i < credentials.length; i++) {
        credentials[i].sealed = true;
        db.get('certificates').chain().find({ id: credentials[i].id }).assign(credentials[i]).write();
      }

      res.send(db.get('certificates'));
    });

    app.delete('/api/certificates/:id', (req, res) => {
      var id = parseInt(req.params.id, 10);
      db.get('certificates').remove({ id: id }).write().then(data =>  res.send(db.get('certificates')));
    });

    app.get('/api/batch/status', (req, res) => {
      res.send(db.get('batchStatus'))
    });
  });
};
