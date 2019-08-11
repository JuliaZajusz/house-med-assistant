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
query punkty{
  points {
    id,
    name,
    coordinate {
      location
    }
  }
}

#wyciaganie wszystkich koordynatow
query koordynaty{
  coordinates {
    id,
    location
  }
}


#wyciaganie wszystkich scieżek
query sciezki{
	paths {
  points {
    id,
    location
  }
  }
}



# #tworzenie nowego punktu
# mutation {
#   newPoint(name: "Wieża Eifflea", location: [4.0, 5.6]) {
#     id
#     name
#     coordinate {
#       id,
#       location
#     }
#   }
# }


# #tworzenie nowego koordynatu
# mutation {
#     newCoordinate(
#     	location: [2.0, 2.0]
#     ){
#         id, location
#     }
# }

#tworzenie nowej scieżki
mutation nowaSciezka{
  newPath(points: [      {
        id: "d3535453-1975-4b4e-a42e-30aaf976dac1",
        location: [
          2,
          2
        ]
      },
      {
        id: "00abfc6f-76d5-4509-9705-89bdbcf018eb",
        location: [
          4,
          5.6
        ]
      }]) {
    id
    # points
  }
}



