package services;

import java.lang.reflect.Type;
import java.util.List;

import org.ubicollab.ubibazaar.core.Category;

import com.google.common.reflect.TypeToken;

public class CategoryService extends UbibazaarService<Category> {

  @Override
  public String getResourceName() {
    return "categories";
  }

  @SuppressWarnings("serial")
  @Override
  public Type getType() {
    return new TypeToken<Category>() {}.getType();
  }

  @SuppressWarnings("serial")
  @Override
  public Type getListType() {
    return new TypeToken<List<Category>>() {}.getType();
  }

}
