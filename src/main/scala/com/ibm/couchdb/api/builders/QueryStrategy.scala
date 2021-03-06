/*
 * Copyright 2016 IBM Corporation
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

package com.ibm.couchdb.api.builders

import com.ibm.couchdb.core.Client
import com.ibm.couchdb.{CouchView, Req}
import upickle.default.Aliases.{R, W}
import upickle.default._
import com.ibm.couchdb._
import fs2.Task

case class QueryBasic[C: R](
    client: Client, db: String, url: String, params: Map[String, String] = Map.empty,
    ids: Seq[String] = Seq.empty) {

  private val queryOps = QueryOps(client)

  def query: Task[C] = {
    ids match {
      case Nil => queryOps.query[C](url, params)
      case _ => queryOps.queryByIds[String, C](url, ids, params)
    }
  }
}

case class QueryView[K: W, C: R](
    client: Client, db: String, design: Option[String], params: Map[String, String] = Map.empty,
    ids: Seq[K] = Seq.empty, view: Option[String], tempView: Option[CouchView]) {

  private lazy val url = (view, design) match {
    case (Some(v), Some(d)) => s"/$db/_design/$d/_view/$v"
    case _ => s"/$db/_temp_view"
  }

  private val queryOps = QueryOps(client)

  def query: Task[C] = {
    ids match {
      case Nil => queryWithoutIds
      case _ => queryByIds
    }
  }

  private def queryWithoutIds: Task[C] = tempView match {
    case Some(t) => queryOps.postQuery[CouchView, C](url, t, params)
    case None => queryOps.query[C](url, params)
  }

  private def queryByIds: Task[C] = tempView match {
    case Some(t) => queryOps.postQuery[Req.ViewWithKeys[K], C](
      url, Req.ViewWithKeys(ids, t), params)
    case None => queryOps.queryByIds[K, C](url, ids, params)
  }
}

