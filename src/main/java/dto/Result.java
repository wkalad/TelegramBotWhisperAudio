package dto;

import java.util.List;

public record Result(List<Update> result, boolean ok) {
}
