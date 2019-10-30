import {gql} from "apollo-boost";
import {client} from "./DefaultService";

export const getAllTags = () => {
  return client
  .query({
      query: gql`{
          tags {
              id,
              name
          }
      }`,
    fetchPolicy: 'network-only'
    }
  );
};

//raczej niepotrzebne, dodawanie taga powinno być bezpośrednio w pacjencie?
export const postTag = (tag) => {
};

export const putTag = (id, newName) => {
  //TODO
}


//nie wiem czy to potrzebne, tagi nieużywane powinny być automatycznie usuwane
export const deleteTag = (name) => {
  return client
  .mutate({
      mutation: gql`
          mutation deleteTag {
              deleteTag(
                  name: "${name}"
              )
          }
      `
    }
  );
};

