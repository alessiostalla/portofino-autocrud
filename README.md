# portofino-autocrud
A Portofino module to build quick CRUD actions against any table in the model.

Just include this as a dependency and access `$API_ROOT/crud/database/schema/table`. Requires Portofino >= 5.3.1.

Such CRUD actions have all properties enabled and writable (except from the primary key which is necessarily read-only), and you cannot change their configuration. Permissions are a) inherited from the application root b) further refined by annotations on tables, if present.

Applications of this module include aiding development and gaining quick insight into the database without leaving the Portofino application (if you have admin rights). It is particularly valuable in conjunction with the "Quick CRUD" feature added upstairs in Portofino 5.3.1. 

Although it can be used in production, this module is probably not flexible enough for an application (due to lack of configurability) and, if not appropriately protected, it can pose a security vulnerability.

## License

This library is distributed under the GNU Affero GPLv3. Please read the LICENSE file for more details.

If you'd like a more business-friendly licensing arrangement, please open an issue or contact the author privately.

## Donations

You can help me maintain this and other projects by donating to [my Patreon](https://www.patreon.com/alessiostalla).
