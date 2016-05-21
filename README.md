# Operational Transformation

##Intruduction

This library is a reference implementation for OT protocol described introduced in Google Wave and described in their [documentation](https://people.apache.org/~al/wave_docs/ApacheWaveProtocol-0.4.pdf)

##Protocol basics

OT protocol may be used to solve consistency problem in a systems where having global locks is not possible as well as loosing stale data, collaborative text editing is an example of such system. Imagine few people writing in a same document and being blocked by each other or some of the changes could gone. To prevent this me should be able to recreated a text having all changes being applied so far as well as local changes and order could be changed safely. 

##Example (2 users)

We could have two users: Alice and Bob, and two local versions of documents, each user sends their changes to another. Documents will look like **DOC(“Hello ”)** and changes may be something like: **SKIP(6)**, **INS(“World!!!”)** or **DEL(6)**

If having a text “Hello ” we will apply operation **SKIP(6)** - our imagine caret will move 6 chars forward and next operation will be applied there, applying **INS(“World!!!”)** then will give us **DOC(“Hello World!!!”)**

If we want to set up communication between 2 users we will need async channel, because we cannot block a user until we sent change to another one, let’s think about this like a queue of changes which should be sent, or *changes that other user haven’t seen yet*.

**Consider following scenario:**
- Alice applies **INS(“Hi ”)** to her empty text
- Operation **INS(“Hi ”)** queued for delivery to Bob
- Bob applies **INS(“Alice”)** and this operation also queued [^If we are using a network protocol which supports ordering (the order we sending messages and order other side will get them is the same - then we can rely on this guarantees, otherwise we would need to add some revision number to our messages)
]
- Bob receives **INS(“Hi ”)** operation [^ we should guarantee that receiving and sending happening in a same working process to prevent situation when messages could be sent and received in a same time]
- Bob should apply received change to his text, but what should be correct order in this case, if Bob will apply message as is and Alice will do the same they will have different texts: **”Hi Alice”** and **“AliceHi ”** - this mean that we need to add ordering information to this operations [^ this should be unique and monotonically increasing sequence, usually vector clocks, Lamport clocks or server issued revision number is used, note that timestamp doesn’t guarantee monotonic increase and uniqueness]
- To make example easier we could use user id (Alice = 1, Bob = 2), this mean that all concurrent changes will be applied first for Alice and then for Bob (since Bob has smaller id)
- After Bob get change from Alice **INS(“Hi ”)** he should transform it though all operation unseen by Alice - we have  **INS(“Alice”)** in Bob’s queue, so Bob does **TRANSFORM(INS(“Hi ”),INS(“Alice”))** operation (which I’ll describe later) which in current case will give us **INS(“Hi ”)** and **SKIP(3)+INS(“Alice”)** as a result (insert “Hi “ in 0 position goes to Bob’s text and change from queue became transformed), because Alice has lower user id and her changes goes before Bob’s.
- Bob applies a change to his text and gets: **”Hi Alice ”** as a result
- Change from Bob’s queue (**SKIP(3)+INS(“Alice”)**) is sent to Alice
- Alice applies this change to her text and gets the same result: **”Hi Alice ”**

##Implementation

As we san in a previous example we need few things to implement this workflow:
- be able to generate changes: inserts, deletes and skips (retains)
- be able to transform changes agains each other

Here how this example will look using this library:

```java
Text aliceText = Text.empty();
Text bobsText = Text.empty();

System.out.printf("'%s'\n", aliceText); // ''
System.out.printf("'%s'\n", bobsText); // ''

Changes aliceChange = aliceText.diff("Hi ");
Changes bobsChange = bobsText.diff("Alice");

aliceText = aliceText.apply(aliceChange);
bobsText = bobsText.apply(bobsChange);

System.out.printf("'%s'\n", aliceText); // 'Hi '
System.out.printf("'%s'\n", bobsText); // 'Alice'

Transform.Result result = Transform.transform(aliceChange, bobsChange);

aliceText = aliceText.apply(result.getRight());
bobsText = bobsText.apply(result.getLeft());

System.out.printf("'%s'\n", aliceText); // 'Hi Alice'
System.out.printf("'%s'\n", bobsText); // 'Hi Alice'
```

##Composition

Changes may be combined with each other using following API:

```java
Changes change1 = Text.empty().diff(“Hi ”);
Changes change2 = Text.wrap(“Hi ”).diff(“Hi Alice”);
Changes batchChange = Compose.compose(change1, change2);
System.out.printf(“‘%s\n’”, Text.empty().apply(batchChange)); // Hi Alice
```

#TODO

* [ ] Implement changes queue 
* [ ] Implement different revision counters: VC, LC, revisions
* [ ] Implement simple rich text operations and markup support