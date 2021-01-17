# widget-service

The *widget-service* is a microservice used to handle Widget as if all the clients work on the same board.

A Widget is an object placed in a cartesian plane, it is described by: 
- Coordinates  X and Y;
- Z-index that represents the order of the Widget and it is unique across Widgets;
- Widget width and height that are non-negative values; and,
- Unique identifier and last modification date.

When a Widget is inserted and updated, if the Z-index is conflicting with other Widget that Widget must be shifted.

## Technical decision

- The UUID was used because UUID is supported by different databases, making the migration easier. Reduce the chances of duplication. UUID is appropriated to be shared cross-system in the environment. 

- The update is done via PUT because the requirements showed that the whole Widget is sent. In case a field is null, that field will not be considered except Z-Index. If the Z-Index is null it means the max Z-Index should be used.

  - That exceptional behavior for the z-index field feels strange but is necessary due to the requirements does not say that the update will be done with the whole entity in the request. It only expects the updated item in the response. Anyway, this is a possible improvement point: by making for the update mandatory to have all the fields.

- The Widget shift, which happens when there is a z-index conflict, bring Widget by Widget from the database incrementing the z-index search. It is done like that to find the very first gap o z-index. It was also possible to bring N Widget which time, but it would be hard to find a good N value.

- The InMemoryRepository handles two Maps, both thread-safe, one collection handles the entity (Widget) and its id, and the second collection works as an index for the Z-index.

- The synchronization is done based on the index (z-index collection) because it is that last updated collection. That way, read from the Widget collection is possible and will not generate inconsistency.

- The widget service does the creation and update in a synchronous block to avoid concurrency issues. To implement that, a command-like approach is used where a sync block is used to execute either creation or update.

