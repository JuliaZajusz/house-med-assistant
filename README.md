# house-med-assistant

# mutation {
#   newSnack(name: "French Fries", amount: 40.5) {
#     id
#     name
#     amount
#   }
# }



# mutation {
#     newReview(snackId:"14d029c6-6fdb-4137-8682-c44faf4af853",
#     text: "Awesome snack!", rating:5
#     ){
#         snackId, text, rating
#     }
# }


# query {
#   snacks {
#     name,
#     reviews {
#       text, rating
#     }
#   }
# }

# wyciąganie wszystkich punktow z ich lokalizacjami z bazy
# query {
#   points {
#     id,
#     name,
#     coordinates {
#       location
#     }
#   }
# }


# mutation {
#   newPoint(name: "Wieża Eifflea") {
#     id
#     name
#   }
# }


# mutation {
#     newCoordinate(
#       pointId:"a8c361a5-ab21-4908-b3bf-1ffc4406172d",
#     	location: [1.0, 2.0]
#     ){
#         pointId, location
#     }
# }

# mutation {
#     updatePoint(
#       id:"a8c361a5-ab21-4908-b3bf-1ffc4406172d",
#         location: [4.0, 3.0]
#     ){
#         id,
#     		name,
#     		coordinates {
#           location
#         }
#     }
# }


