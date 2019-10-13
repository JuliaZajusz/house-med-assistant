import {gql} from "apollo-boost";
import {client} from "./DefaultService";

// export const getSalesmanSet = (id) => {
//     return null;
// }

export const getAllPatients = () => {
  return client
  .query({
      query: gql`{
          patients {
              id,
              lastName,
              firstName,
              address,
              coordinate {
                  id,
                  location
              },
              tags {
                  # id,
                  name
              }
          }
      }`
    }
  );
};

export const getAllPatientsTyText = (searchedText) => {
  if (searchedText && searchedText !== "") {
    return client
    .query({
        query: gql`{
            findPatientsByFullTextSearch(searchedText: "${searchedText}") {
                id,
                lastName,
                firstName,
                address,
                coordinate {
                    id,
                    location
                },
                tags {
                    # id,
                    name
                }
            }
        }`
      }
    );
  } else {
    return getAllPatients();
  }
};

export const getPatientsByNameAndAddressRespectingTags = (searchedText, tags) => {
  if (tags && tags.length > 0) {
    const stringTags = "[\"" + tags.join("\", \"") + "\"]";
    console.log("searchedText", searchedText, "tags", tags, stringTags);
    return client
    .query({
        query: gql`{
            findPatientsByTextRespectingTags(searchedText: "${searchedText}", tags: ${stringTags}) {
                id,
                lastName,
                firstName,
                address,
                coordinate {
                    id,
                    location
                },
                tags {
                    # id,
                    name
                }
            }
        }`
      }
    );
  } else {
    return getAllPatientsTyText(searchedText);
  }
};

// export const getPatientsByTags = (tags) => {
//   if (tags && tags.length > 0) {
//     return client
//     .query({
//         query: gql`{
//             findPatientsByTags(tags: ${tags}) {
//                 id,
//                 lastName,
//                 firstName,
//                  address,
//                 coordinate {
//                     id,
//                     location
//                 },
//                 tags {
//                     # id,
//                     name
//                 }
//             }
//         }`
//       }
//     );
//   } else {
//     return getAllPatients();
//   }
// };


//TODO zmienić na płynne wyszukiwanie po imieniu i nazwisku, ograniczone tagami
export const getPatientsByLastNameAndFirstName = (lastName, firstName) => {
  return client
  .query({
      query: gql`{
          findPatientsByLastNameAndFirstName(lastName:${lastName}, firstName: ${firstName}) {
              id,
              lastName,
              firstName,
              coordinate {
                  id,
                  location
              },
              tags {
                  # id,
                  name
              }
          }
      }`
    }
  );
};


export const postPatient = (patient) => {
  const lastName = patient.lastName;
  const firstName = patient.firstName;
  const location = patient.location;
  const tags = patient.tags;
  return client
  .mutate({
      mutation: gql`
          mutation newPatient {
              newPatient(
                  lastName: ${lastName},
                  firstName: ${firstName},
                  location: ${location},
                  tags: ${tags},
              ) {
                  id,
                  lastName,
                  firstName,
                  coordinate {
                      id,
                      location
                  },
                  tags {
                      id,
                      name
                  }
              }
          }
      `
    }
  );
};

export const putPatient = (id, patient) => {
  //TODO
}

export const deletePatient = (id) => {
  return client
  .mutate({
      mutation: gql`
          mutation deletePatient {
              deletePatient(
                  id: "${id}"
              )
          }
      `
    }
  );
};

