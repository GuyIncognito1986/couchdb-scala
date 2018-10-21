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

package com.ibm.couchdb.spec

import com.ibm.couchdb._
import com.ibm.couchdb.api.Databases
import com.ibm.couchdb.core.Client
import com.ibm.couchdb.implicits.{TaskImplicits, UpickleImplicits}
import org.specs2.matcher._
import org.specs2.mutable._
import org.specs2.scalaz.DisjunctionMatchers
import org.specs2.specification.AllExpectations

import scalaz._
import fs2.Task

trait CouchDbSpecification extends Specification with
    Fixtures with
    AllExpectations with
    DisjunctionMatchers with
    TaskImplicits with
    UpickleImplicits {
  sequential

  val client = new Client(
    Config(SpecConfig.couchDbHost, SpecConfig.couchDbPort, https = false, None))

  def await[T](future: Task[T]): Either[Throwable, T] = future.unsafeAttemptRun()

  def awaitRight[T](future: Task[T]): T = {
    val res = await(future)
    res.isRight mustEqual true
    res.toOption.get
  }

  def awaitOk[T](future: Task[Res.Ok]): MatchResult[Any] = {
    val res = await(future)
    res.isRight mustEqual true
    res.right mustEqual Res.Ok(ok = true)
  }

  def awaitDocOk[D](future: Task[Res.DocOk]): MatchResult[Any] = {
    checkDocOk(awaitRight(future))
  }

  def awaitDocOk[D](future: Task[Res.DocOk], id: String): MatchResult[Any] = {
    checkDocOk(awaitRight(future), id)
  }

  def awaitLeft(future: Task[_]): Res.Error = {
    val res = await(future)
    res.isLeft mustEqual true
    val exception = res.left
    exception.asInstanceOf[CouchException[Res.Error]].content
  }

  def awaitError(future: Task[_], error: String): MatchResult[Any] = {
    val res = awaitLeft(future)
    res must beLike {
      case Res.Error(actualError, _, _, _, _) => actualError mustEqual error
    }
  }

  def beUuid: Matcher[String] = haveLength(32)

  def beRev: Matcher[String] = (_: String).length must beGreaterThan(32)

  def checkDocOk(doc: Res.DocOk): MatchResult[Any] = {
    (doc.ok mustEqual true) and (doc.id must not beEmpty) and (doc.rev must beRev)
  }

  def checkDocOk(doc: Res.DocOk, id: String): MatchResult[Any] = {
    checkDocOk(doc) and (doc.id mustEqual id)
  }

  def recreateDb(databases: Databases, name: String): Either[Throwable, Res.Ok] = await {
    databases.delete(name).or(Task.now(Res.Ok()))
    databases.create(name)
  }
}
