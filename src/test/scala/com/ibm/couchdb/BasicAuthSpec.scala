/*
 * Copyright 2015 IBM Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibm.couchdb

import com.ibm.couchdb.spec.{CouchDbSpecification, SpecConfig}

class BasicAuthSpec extends CouchDbSpecification {

  val couch      = CouchDb(SpecConfig.couchDbHost, SpecConfig.couchDbPort, false, "", "")
  val couchAdmin = CouchDb(
    SpecConfig.couchDbHost,
    SpecConfig.couchDbPort,
    https = false,
    SpecConfig.couchDbUsername,
    SpecConfig.couchDbPassword)

  val db       = "couchdb-scala-basic-auth-spec"
  val adminUrl = s"/_config/admins/${SpecConfig.couchDbUsername}"

  "Basic authentication" >> {
    "Only admin can create and delete databases" >> {
      await(couchAdmin.dbs.delete(db).ignoreError)
      awaitError(couch.dbs.create(db), "Error")
      awaitOk(couchAdmin.dbs.create(db))
      awaitError(couch.db(db, typeMapping).docs.create(fixAlice), "Error")
    }
  }
}
