const userRoutes = require('./user_routes');
const issuerRoutes = require('./issuer_routes');

module.exports = function (app, db) {
  userRoutes(app, db);
  issuerRoutes(app, db);
};
